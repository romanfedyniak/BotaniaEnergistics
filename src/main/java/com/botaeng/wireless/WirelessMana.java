package com.botaeng.wireless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import vazkii.botania.api.mana.ManaItemsEvent;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.WirelessTerminalToggle;
import appeng.api.features.WirelessTerminalToggles;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.ISecurityGrid;
import appeng.helpers.WirelessTerminalAccess;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.me.helpers.PlayerSource;
import appeng.util.Platform;

import com.botaeng.BotaEng;
import com.botaeng.BotaEngConfig;
import com.botaeng.BotaEngItems;
import com.botaeng.me.ManaKey;

/**
 * Botania's tools and armour drawing mana from an ME network through a wireless terminal the player carries.
 * <p>
 * Botania asks for the mana items a player has, and this answers with one more: a stand-in for every network a
 * terminal reaches, put after everything the player really carries, so a tablet or a ring is spent first. It
 * only gives - nothing is ever charged into a network this way. A terminal answers on the same terms as when it
 * is opened: linked, charged, in range of an access point, and the player allowed to take things out.
 */
public final class WirelessMana {

    /** What the terminal's own battery pays each time mana is drawn, on top of what the network's move costs. */
    private static final double COST_PER_REQUEST = 0.5;

    public static final WirelessTerminalToggle TOGGLE = WirelessTerminalToggle.builder(BotaEng.id("wireless_mana"))
            .defaultValue(true)
            .icons(BotaEng.id("textures/guis/states.png"), 0, 0, 16, 0)
            .tooltip("gui.botaeng.wireless_mana", "gui.botaeng.wireless_mana.on", "gui.botaeng.wireless_mana.off")
            .build();

    /** Which networks a stand-in speaks for; the stand-in lives no longer than Botania's one request. */
    private static final Map<ItemStack, WirelessMana> SOURCES = Collections.synchronizedMap(new WeakHashMap<>());

    /** Worked out once a tick per player: Botania may ask many times in one. */
    private static final Map<EntityPlayer, Cached> CACHE = new WeakHashMap<>();

    private final EntityPlayer player;
    private final List<WirelessTerminalGuiObject> terminals;

    private WirelessMana(final EntityPlayer player, final List<WirelessTerminalGuiObject> terminals) {
        this.player = player;
        this.terminals = terminals;
    }

    public static void register() {
        WirelessTerminalToggles.register(TOGGLE);
        MinecraftForge.EVENT_BUS.register(new WirelessMana.Listener());
    }

    @Nullable
    static WirelessMana of(final ItemStack standIn) {
        return SOURCES.get(standIn);
    }

    /**
     * Everything the networks would give now, as far as Botania can count. Botania takes what this says without
     * checking what it then gets, so it must be no more than a draw would really bring - the battery included.
     */
    int available() {
        long total = 0;
        for (final WirelessTerminalGuiObject terminal : this.terminals) {
            final long stored = terminal.getInventory().extract(ManaKey.INSTANCE, Integer.MAX_VALUE,
                    Actionable.SIMULATE, new PlayerSource(this.player, terminal));
            total += Math.min(stored, affordable(terminal));
            if (total >= Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
        }
        return (int) total;
    }

    /** As much mana as the terminal's battery can pay the network to move, less the cost of asking. */
    private static long affordable(final WirelessTerminalGuiObject terminal) {
        final ItemStack stack = terminal.getItemStack();
        if (!(stack.getItem() instanceof IAEItemPowerStorage)) {
            return Long.MAX_VALUE;
        }
        final double power = ((IAEItemPowerStorage) stack.getItem()).getAECurrentPower(stack) - COST_PER_REQUEST;
        if (power <= 0) {
            return 0;
        }
        return (long) Math.min(Long.MAX_VALUE,
                PowerMultiplier.CONFIG.divide(power) * ManaKey.INSTANCE.getAmountPerOperation());
    }

    void extract(final int amount) {
        long left = amount;
        for (final WirelessTerminalGuiObject terminal : this.terminals) {
            if (left <= 0) {
                return;
            }
            final long taken = Platform.poweredExtraction(terminal, terminal.getInventory(), ManaKey.INSTANCE, left,
                    new PlayerSource(this.player, terminal), Actionable.MODULATE);
            if (taken > 0) {
                terminal.extractAEPower(COST_PER_REQUEST, Actionable.MODULATE, PowerMultiplier.CONFIG);
                left -= taken;
            }
        }
    }

    /**
     * @return the networks the player's terminals reach, each once, or null if there is none.
     */
    @Nullable
    private static WirelessMana forPlayer(final EntityPlayer player) {
        final long tick = player.world.getTotalWorldTime();
        final Cached cached = CACHE.get(player);
        if (cached != null && cached.tick == tick) {
            return cached.source;
        }
        final List<WirelessTerminalGuiObject> terminals = new ArrayList<>();
        final List<IGrid> grids = new ArrayList<>();
        for (final WirelessTerminalGuiObject terminal : WirelessTerminalAccess.reachable(player, TOGGLE::isOn)) {
            final IGridNode node = terminal.getActionableNode();
            final IGrid grid = node == null ? null : node.getGrid();
            if (grid == null || grids.contains(grid)) {
                continue;
            }
            final ISecurityGrid security = grid.getCache(ISecurityGrid.class);
            if (security != null && !security.hasPermission(player, SecurityPermissions.EXTRACT)) {
                continue;
            }
            grids.add(grid);
            terminals.add(terminal);
        }
        final WirelessMana source = terminals.isEmpty() ? null : new WirelessMana(player, terminals);
        CACHE.put(player, new Cached(tick, source));
        return source;
    }

    private static final class Cached {

        private final long tick;
        @Nullable
        private final WirelessMana source;

        private Cached(final long tick, @Nullable final WirelessMana source) {
            this.tick = tick;
            this.source = source;
        }
    }

    public static final class Listener {

        /** Last, so the stand-in comes after anything another mod adds too. */
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onManaItems(final ManaItemsEvent event) {
            final EntityPlayer player = event.getEntityPlayer();
            // Only the server can reach a network; the client never counts it.
            if (!BotaEngConfig.wirelessMana || player.world.isRemote || player instanceof FakePlayer) {
                return;
            }
            final WirelessMana source = forPlayer(player);
            if (source != null) {
                final ItemStack standIn = new ItemStack(BotaEngItems.NETWORK_MANA);
                SOURCES.put(standIn, source);
                event.add(standIn);
            }
        }
    }
}
