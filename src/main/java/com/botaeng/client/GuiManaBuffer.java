package com.botaeng.client;

import java.util.Collections;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import vazkii.botania.client.core.handler.HUDHandler;

import com.botaeng.BotaEngItems;
import com.botaeng.container.ContainerManaBuffer;
import com.botaeng.me.ManaKeyType;
import com.botaeng.tile.TileManaBuffer;

/**
 * A three-row chest with Botania's mana bar beside the inventory label.
 */
public class GuiManaBuffer extends GuiContainer {

    private static final ResourceLocation CHEST = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final int ROWS = 3;
    private static final int BAR_X = 66;
    private static final int BAR_Y = 75;
    private static final int BAR_WIDTH = 102;
    private static final int BAR_HEIGHT = 5;

    private final ContainerManaBuffer container;
    private final InventoryPlayer player;

    public GuiManaBuffer(final InventoryPlayer player, final TileManaBuffer tile) {
        this(new ContainerManaBuffer(player, tile), player);
    }

    private GuiManaBuffer(final ContainerManaBuffer container, final InventoryPlayer player) {
        super(container);
        this.container = container;
        this.player = player;
        this.ySize = 114 + ROWS * 18;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        final int x = mouseX - this.guiLeft;
        final int y = mouseY - this.guiTop;
        if (x >= BAR_X && x < BAR_X + BAR_WIDTH && y >= BAR_Y && y < BAR_Y + BAR_HEIGHT) {
            this.drawHoveringText(Collections.singletonList(I18n.format("gui.botaeng.mana_buffer.mana",
                    String.format("%,d", this.container.getMana()), String.format("%,d", this.container.getCapacity()),
                    String.format("%.2f", (double) this.container.getMana() / ManaKeyType.POOL))), mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        this.fontRenderer.drawString(BotaEngItems.MANA_BUFFER.getLocalizedName(), 8, 6, 0x404040);
        this.fontRenderer.drawString(this.player.getDisplayName().getUnformattedText(), 8, this.ySize - 94, 0x404040);
        GlStateManager.enableBlend();
        HUDHandler.renderManaBar(BAR_X, BAR_Y, 0x0000FF, 0.75F, this.container.getMana(),
                Math.max(1, this.container.getCapacity()));
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        this.mc.getTextureManager().bindTexture(CHEST);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, ROWS * 18 + 17);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop + ROWS * 18 + 17, 0, 126, this.xSize, 96);
    }
}
