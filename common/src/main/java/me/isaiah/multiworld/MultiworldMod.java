/**
 * Multiworld Mod
 * Copyright (c) 2021-2022 by Isaiah.
 */
package me.isaiah.multiworld;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.isaiah.multiworld.command.CreateCommand;
import me.isaiah.multiworld.command.SetspawnCommand;
import me.isaiah.multiworld.command.SpawnCommand;
import me.isaiah.multiworld.command.TpCommand;
import me.isaiah.multiworld.perm.Perm;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.io.File;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

//import me.isaiah.lib.IText;

/**
 * Multiworld version 1.3
 */
public class MultiworldMod {

    public static final String MOD_ID = "multiworld";
    public static MinecraftServer mc;
    public static String CMD = "mw";
    public static ICreator world_creator;

	// Mod Version
	public static final String VERSION = "1.6";
    
    public static void setICreator(ICreator ic) {
        world_creator = ic;
    }

    public static ServerWorld create_world(String id, RegistryKey<DimensionType> dim, ChunkGenerator gen, Difficulty dif, long seed) {
        return world_creator.create_world(id, dim,gen,dif, seed);
    }

    // On mod init
    public static void init() {
        System.out.println(" Multiworld init");
    }
    public static ICreator get_world_creator() {
        return world_creator;
    }

    // On server start
    public static void on_server_started(MinecraftServer mc) {
        MultiworldMod.mc = mc;
		
		File cfg_folder = new File("config");
		if (cfg_folder.exists()) {
			File folder = new File(cfg_folder, "multiworld");
			File worlds = new File(folder, "worlds");
			if (worlds.exists()) {
				for (File f : worlds.listFiles()) {
					if (f.getName().equals("minecraft")) {
						continue;
					}
					for (File fi : f.listFiles()) {
						String id = f.getName() + ":" + fi.getName().replace(".yml", "");
						System.out.println("Found saved world " + id);
						CreateCommand.reinit_world_from_config(mc, id);
					}
				}
			}
		}
    }
    
    // On command register
    public static void register_commands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(CMD)
                    .requires(source -> {
                        try {
                            return Perm.has(source.getPlayerOrThrow(), "multiworld.cmd") ||
                                    Perm.has(source.getPlayerOrThrow(), "multiworld.admin");
                        } catch (Exception e) {
                            return source.hasPermissionLevel(1);
                        }
                    }) 
                        .executes(ctx -> {
                            return broadcast(ctx, ctx.getSource(), Formatting.AQUA, null);
                        })
                        .then(argument("message", greedyString()).suggests(new InfoSuggest())
                                .executes(ctx -> {
                                    try {
                                        return broadcast(ctx, ctx.getSource(), Formatting.AQUA, getString(ctx, "message") );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        return 1;
                                    }
                                 }))); 
    }
    
    public static int broadcast(CommandContext ctx, ServerCommandSource source, Formatting formatting, String message) throws CommandSyntaxException {
        final ServerPlayerEntity plr = source.getPlayer();

        if (null == message) {
            plr.sendMessage(text("Usage:", Formatting.AQUA), false);
            return 1;
        }

       // boolean ALL = Perm.has(plr, "multiworld.admin");
        String[] args = message.split(" ");

        if (args[0].equalsIgnoreCase("setspawn") && (Perm.has(plr, "multiworld.setspawn") )) {
            return SetspawnCommand.run(mc, plr, args);
        }

        if (args[0].equalsIgnoreCase("spawn") && (Perm.has(plr, "multiworld.spawn")) ) {
            return SpawnCommand.run(mc, plr, args);
        }

        if (args[0].equalsIgnoreCase("tp") ) {
            /*
            if (!(ALL || Perm.has(plr, "multiworld.tp"))) {
                plr.sendMessage(Text.of("No permission! Missing permission: multiworld.tp"), false);
                return 1;
            }
            */
            if (args.length == 1) {
                plr.sendMessage(text_plain("Usage: /" + CMD + " tp <world> <subject> <x> <y> <z> <yaw> <pitch>"), false);
                return 0;
            }
            return TpCommand.run(mc, plr, args);
        }

        if (args[0].equalsIgnoreCase("list") ) {
            if (!( Perm.has(plr, "multiworld.cmd"))) {
                plr.sendMessage(Text.of("No permission! Missing permission: multiworld.cmd"), false);
                return 1;
            }
            plr.sendMessage(text("All Worlds:", Formatting.AQUA), false);
            mc.getWorlds().forEach(world -> {
                String name = world.getRegistryKey().getValue().toString();
                if (name.startsWith("multiworld:")) name = name.replace("multiworld:", "");

                plr.sendMessage(text_plain("- " + name), false);
            });
        }

        if (args[0].equalsIgnoreCase("version") && ( Perm.has(plr, "multiworld.cmd")) ) {
            plr.sendMessage(text_plain("Mutliworld Mod version " + VERSION), false);
            return 1;
        }

        if (args[0].equalsIgnoreCase("create") ) {
            if (!( Perm.has(plr, "multiworld.create"))) {
                plr.sendMessage(Text.of("No permission! Missing permission: multiworld.create"), false);
                return 1;
            }
            return CreateCommand.run(mc, plr, args);
        }

        return Command.SINGLE_SUCCESS; // Success
    }
    
    
	// TODO: this could be better
	public static Text text(String txt, Formatting color) {
		return world_creator.colored_literal(txt, color);
	}
	
	public static Text text_plain(String txt) {
		return Text.of(txt);
	}
	
}
