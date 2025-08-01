package io.github.moulberry.betterscaledgui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Mod(modid = BetterScaledGUI.MODID, version = BetterScaledGUI.VERSION, clientSideOnly = true)
public class BetterScaledGUI {
    public static final String MODID = "betterscaledhotbar";
    public static final String VERSION = "1.1-REL";

    private static final String PREFIX = EnumChatFormatting.GRAY+"["+EnumChatFormatting.LIGHT_PURPLE+"R"+EnumChatFormatting.GRAY+"] ";

    SimpleCommand setScalingCommand = new SimpleCommand("inventoryscale", new SimpleCommand.ProcessCommandRunnable() {
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if(args.length > 1) {
                sender.addChatMessage(new ChatComponentText(PREFIX+EnumChatFormatting.RED+"Too many arguments. Use '/inventoryscale help' for assistance"));
                return;
            }
            if(args.length == 0 ||
                    args[0].equalsIgnoreCase("help")) {
                sender.addChatMessage(new ChatComponentText(PREFIX+"           Help"));
                sender.addChatMessage(new ChatComponentText(PREFIX+"Usage: /inventoryscale [scaling]"));
                sender.addChatMessage(new ChatComponentText(PREFIX+"Scaling may be a number between 1-5, or"));
                sender.addChatMessage(new ChatComponentText(PREFIX+"small/normal/large/auto"));
                sender.addChatMessage(new ChatComponentText(PREFIX+"Use '/inventoryscale off' to disable scaling"));
                return;
            }
            if(args[0].equalsIgnoreCase("off") ||
                    args[0].equalsIgnoreCase("none")) {
                ScaledResolutionOverride.setDesiredScaleOverride(1);
                sender.addChatMessage(new ChatComponentText(PREFIX+EnumChatFormatting.GREEN+"Disabled inventory scaling"));
                saveConfig();
                return;
            }
            float scaling;
            if(args[0].equalsIgnoreCase("small") || args[0].equalsIgnoreCase("s")) {
                scaling = 0.5f;
            } else if(args[0].equalsIgnoreCase("normal") || args[0].equalsIgnoreCase("n")) {
                scaling = 1;
            } else if(args[0].equalsIgnoreCase("large") || args[0].equalsIgnoreCase("l")) {
                scaling = 2;
            } else if(args[0].equalsIgnoreCase("auto") || args[0].equalsIgnoreCase("a")) {
                scaling = 3;
            } else {
                try {
                    scaling = Integer.parseInt(args[0]);
                } catch(Exception e) {
                    sender.addChatMessage(new ChatComponentText(
                            PREFIX+EnumChatFormatting.RED+"Invalid scaling identifier. Use '/inventoryscale help' for assistance"));
                    return;
                }
            }
            if(scaling < 1) {
                ScaledResolutionOverride.setDesiredScaleOverride(-1);
                sender.addChatMessage(new ChatComponentText(PREFIX+EnumChatFormatting.GREEN+"Disabled inventory scaling"));
                saveConfig();
                return;
            } else if(scaling > 5) {
                sender.addChatMessage(new ChatComponentText(PREFIX+EnumChatFormatting.RED+"Invalid scaling. Must be between 1-5"));
                return;
            }
            sender.addChatMessage(new ChatComponentText(PREFIX+EnumChatFormatting.GREEN+"Set inventory scaling to: " + scaling));
            ScaledResolutionOverride.setDesiredScaleOverride(scaling);
            saveConfig();
        }
    });

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File configFile = null;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(setScalingCommand);

        File configDir = new File(event.getModConfigurationDirectory(), "betterscaledhotbar");
        configDir.mkdirs();
        configFile = new File(configDir, "config.json");

        loadConfig();
    }

    private void saveConfig() {
        if(configFile != null) {
            try {
                JsonObject object = new JsonObject();
                object.addProperty("scale", ScaledResolutionOverride.getDesiredScaleOverride());
                configFile.createNewFile();

                try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))) {
                    writer.write(gson.toJson(object));
                }
            } catch(Exception e) {
                System.err.println("FAILED TO WRITE CONFIG!");
                e.printStackTrace();
            }
        }

    }

    private void loadConfig() {
        if(configFile != null) {
            try {
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    ScaledResolutionOverride.setDesiredScaleOverride(json.get("scale").getAsInt());
                }
            } catch(Exception e) {
                System.err.println("FAILED TO LOAD CONFIG!");
                e.printStackTrace();
            }
        }
    }

}
