package net.graphich.ambiotic.main;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.registry.GameData;
import net.graphich.ambiotic.registries.VariableRegistry;
import net.graphich.ambiotic.scanners.BlockScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;

/**
 * Created by jim on 9/11/2014.
 */
public class DebugGui extends GuiScreen {

    // Rendering variables
    private static final int TEXT_COLOR = 16777215;
    private static final int X_OFFSET = 8;
    private static final int Y_OFFSET = 6;
    private static final int Y_INC = 10;

    // Key variables
    private static final int DEBUG_KEY = 0;
    private static final int SHEETS_KEY = 1;
    private static final int OFF_KEY = 2;
    protected int mState = OFF_KEY;
    private static final String[] DESCRIPTIONS = {"Variable Debug", "Sheet Debug", "Turn Off Debug"};
    private static final int[] KEY_VALUES = {Keyboard.KEY_COMMA, Keyboard.KEY_PERIOD, Keyboard.KEY_M};
    private final KeyBinding[] KEYS;
    protected FontRenderer mFontRenderer;
    protected BlockScanner mBlockScanner;

    public DebugGui() {
        KEYS = new KeyBinding[DESCRIPTIONS.length];
        for (int i = 0; i < DESCRIPTIONS.length; i++) {
            KEYS[i] = new KeyBinding(DESCRIPTIONS[i], KEY_VALUES[i], "Ambiotic Mod");
            ClientRegistry.registerKeyBinding(KEYS[i]);
        }
        mFontRenderer = Minecraft.getMinecraft().fontRenderer;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
            mState = OFF_KEY;
        } else {
            if (KEYS[DEBUG_KEY].isPressed()) mState = DEBUG_KEY;
            if (KEYS[SHEETS_KEY].isPressed()) mState = SHEETS_KEY;
            if (KEYS[OFF_KEY].isPressed()) mState = OFF_KEY;
        }
    }

    @SubscribeEvent
    public void draw(RenderGameOverlayEvent event) {
        switch (mState) {
            case DEBUG_KEY:
                drawVariables();
                break;
            case SHEETS_KEY:
                drawScanner();
            default:
                break;
        }
    }

    public void addScanner(BlockScanner scanner) {
        mBlockScanner = scanner;
    }

    public void drawScanner() {
        int y = Y_OFFSET;
        int x = X_OFFSET;
        mFontRenderer.drawString("Scanner", x, y, TEXT_COLOR, true);
        y += Y_INC;
        if (mBlockScanner == null || !mBlockScanner.scanFinished()) {
            return;
        }
        for (Integer blockId : mBlockScanner.keySet()) {
            String name = GameData.getBlockRegistry().getObjectById(blockId).getLocalizedName();
            mFontRenderer.drawString(name + " : " + mBlockScanner.getCount(blockId), x, y, TEXT_COLOR, true);
            y += Y_INC;
        }
    }

    public void drawVariables() {
        int y = Y_OFFSET;
        int x = X_OFFSET;

        if (!VariableRegistry.instance().isFrozen()) {
            mFontRenderer.drawString("Registry is not frozen", x, y, TEXT_COLOR, true);
            return;
        }

        for (String name : VariableRegistry.instance().names()) {
            int v = VariableRegistry.instance().value(name);
            name += " = " + v;
            mFontRenderer.drawString(name, x, y, TEXT_COLOR, true);
            y += Y_INC;
        }
    }
}
