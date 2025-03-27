package net.raynna.raynnarpg.client.ui.floating_text;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class FloatingText {
    private static final Minecraft mc = Minecraft.getInstance();

    private static final float GUI_LAYER = 5000f;

    // Timing controls
    private static final long DEFAULT_DURATION = 3000; // Total visible time in milliseconds
    private static final float DEFAULT_SPEED = 1000f; // Higher = slower upward movement

    private static final float SCALE_DISTANCE_FACTOR = 5f; // How quickly text shrinks with distance
    private static final float DEFAULT_SCALE = 1.0f;
    private static final float SCREEN_SPACE_SCALE = 0.75f; // Separate scale for screen-space text


    private static final boolean DEBUG = true; // Toggle debug output

    private final Component text;
    private final Vec3 position;
    private final long createTime;
    private final boolean screenSpace;
    private final int color;
    private final long duration;
    private final float scale;

    private FloatingText(Component text, Vec3 position, boolean screenSpace,
                         int color, long duration, float scale) {
        this.text = text;
        this.position = position;
        this.screenSpace = screenSpace;
        this.color = color;
        this.duration = duration;
        this.scale = scale;
        this.createTime = System.currentTimeMillis();
    }

    @OnlyIn(Dist.CLIENT)
    public static FloatingText createOnBlock(String text, BlockPos pos) {
        return new FloatingText(
                Component.literal(text),
                new Vec3(pos.getX(), pos.getY(), pos.getZ()),
                false,
                0xFFFFFF,
                DEFAULT_DURATION,
                1.0f
        );
    }

    public static FloatingText createCentered(String text, float centerX, float centerY) {
        return new FloatingText(
                Component.literal(text),
                new Vec3(centerX, centerY, 1500),
                true,
                0xFFFFFF,
                DEFAULT_DURATION,
                1.0f
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static FloatingText createCentered(String text) {
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        return new FloatingText(
                Component.literal(text),
                new Vec3(width / 2f, height / 2f, 1500),
                true,
                0xFFFFFF,
                DEFAULT_DURATION,
                1.0f
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static FloatingText createScreenSpace(String text, double x, double y) {
        return new FloatingText(
                Component.literal(text),
                new Vec3(x, y, 1500),
                true,
                0xFFFFFF,
                DEFAULT_DURATION,
                DEFAULT_SCALE
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static FloatingText createWorldSpace(String text, double x, double y, double z) {
        return new FloatingText(
                Component.literal(text),
                new Vec3(x, y, z),
                false,
                0xFFFFFF,
                DEFAULT_DURATION,
                DEFAULT_SCALE
        );
    }

    public static FloatingText sendOnWorld(String text, double x, double y, double z) {
        return new FloatingText(
                Component.literal(text),
                new Vec3(x, y, z),
                false,
                0xFFFFFF,
                DEFAULT_DURATION,
                DEFAULT_SCALE
        );
    }

    public static FloatingText sendOnScreen(String text, double x, double y) {
        return new FloatingText(
                Component.literal(text),
                new Vec3(x, y, 1500), // High Z-level for screen
                true,
                0xFFFFFF,
                DEFAULT_DURATION,
                SCREEN_SPACE_SCALE
        );
    }

    public void render(GuiGraphics guiGraphics, Camera camera) {
        if (isExpired()) return;

        float progress = (System.currentTimeMillis() - createTime) / (float) duration;
        float fade = 1.0f - (float) Math.pow(progress, 2);
        fade = Math.max(0.1f, Math.min(1, fade));
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        if (screenSpace) {
            renderScreenSpace(guiGraphics, fade);
        } else {
            renderWorldSpace(guiGraphics, camera, fade);
        }

        poseStack.popPose();
    }


    private void renderWorldSpace(GuiGraphics guiGraphics, Camera camera, float fade) {
        // Calculate vertical float offset
        float yOffset = (float) (System.currentTimeMillis() - createTime) / DEFAULT_SPEED; // Standardized speed

        ScreenPosition screenPos = calculateScreenPosition(position.add(0, yOffset, 0), camera);

        if (screenPos == null || screenPos.isBehindCamera) {
            return;
        }

        // Apply distance scaling (simplified formula)
        float finalScale = (float) (scale * (SCALE_DISTANCE_FACTOR / Math.max(1, screenPos.distance)));

        renderText(guiGraphics, screenPos.x, screenPos.y, finalScale, fade, 0);
    }

    private void renderScreenSpace(GuiGraphics guiGraphics, float fade) {
        renderText(
                guiGraphics,
                (float) position.x,
                (float) position.y,
                SCREEN_SPACE_SCALE,
                fade,
                GUI_LAYER);
    }

    private void renderText(GuiGraphics guiGraphics, float x, float y, float scale, float fade, float zLevel) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, zLevel);
        poseStack.scale(scale, scale, 1f);

        if (screenSpace) {
            RenderSystem.disableDepthTest(); // Disable for screen-space elements
        } else {
            RenderSystem.enableDepthTest(); // Enable for world-space elements
        }

        int textWidth = mc.font.width(text);
        int alpha = (int)(0xFF * fade);

        // Background
        guiGraphics.fill(-textWidth/2-2, -2, textWidth/2+2, 10, (alpha/2 << 24));

        int finalColor = (alpha << 24) | (color & 0x00FFFFFF);

        // Text
        guiGraphics.drawString(mc.font, text, -textWidth/2, 0, finalColor);

        poseStack.popPose();
        RenderSystem.enableDepthTest();
    }

    // Calculation helpers
    private ScreenPosition calculateScreenPosition(Vec3 worldPos, Camera camera) {
        Vec3 viewPos = worldPos.subtract(camera.getPosition());
        double distance = viewPos.length();

        Vector3f look = camera.getLookVector();
        Vector3f up = camera.getUpVector();
        Vector3f right = new Vector3f(look).cross(up);

        float vx = (float) viewPos.dot(new Vec3(right.x(), right.y(), right.z()));
        float vy = (float) viewPos.dot(new Vec3(up.x(), up.y(), up.z()));
        float vz = (float) viewPos.dot(new Vec3(look.x(), look.y(), look.z()));

        if (vz <= 0.1f) {
            return new ScreenPosition(0, 0, distance, true);
        }

        float aspect = (float) mc.getWindow().getWidth() / mc.getWindow().getHeight();
        float fov = (float) Math.toRadians(mc.options.fov().get());
        float tanFOV = (float) Math.tan(fov / 2);

        float sx = vx / (vz * tanFOV * aspect);
        float sy = vy / (vz * tanFOV);

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        float screenX = (sx + 1.0f) / 2.0f * screenWidth;
        float screenY = (1.0f - (sy + 1.0f) / 2.0f) * screenHeight;

        return new ScreenPosition(screenX, screenY, distance, false);
    }

    private float calculateFade() {
        // Quadratic fade (stays visible longer then fades quickly)
        return 1.0f - (float) Math.pow((System.currentTimeMillis() - createTime) / (float) duration, 2);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createTime >= duration;
    }

    // Debug logging
    private void logCreation() {
        if (DEBUG) System.out.printf("[FloatingText] Created at %s: %s (ScreenSpace: %b, WorldFloating: %b)%n",
                position.toString(), text.getString(), screenSpace);
    }

    private void logExpiration() {
        if (DEBUG) System.out.println("[FloatingText] Expired: " + text.getString());
    }

    public FloatingText withColor(int color) {
        return new FloatingText(text, position, screenSpace, color, duration, scale);
    }

    public FloatingText withDuration(long newDuration) {
        return new FloatingText(text, position, screenSpace, color, newDuration, scale);
    }

    public FloatingText withScale(float newScale) {
        return new FloatingText(text, position, screenSpace, color, duration, newScale);
    }

    private static class ScreenPosition {
        public final float x, y;
        public final double distance;
        public final boolean isBehindCamera;

        public ScreenPosition(float x, float y, double distance, boolean isBehindCamera) {
            this.x = x;
            this.y = y;
            this.distance = distance;
            this.isBehindCamera = isBehindCamera;
        }
    }
}