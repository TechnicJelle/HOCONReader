package com.technicjelle;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.util.NamingSchemes;

import java.nio.file.Files;
import java.nio.file.Path;

public class HOCONReader {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Provide file(s) to read. It/they will be converted to JSON and printed to stdout.");
			System.err.println("If multiple files are provided, they will be printed right after each other, separated by a \\0 (null terminator).");
			System.exit(1);
			return;
		}

		final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
				.defaultNamingScheme(NamingSchemes.PASSTHROUGH)
				.build();

		final HoconConfigurationLoader.Builder hoconBuilder = HoconConfigurationLoader.builder()
				.defaultOptions(opts -> opts.serializers(build -> build.registerAnnotatedObjects(customFactory)));

		final JacksonConfigurationLoader.Builder jacksonBuilder = JacksonConfigurationLoader.builder()
				.headerMode(HeaderMode.NONE);

		final int argsLength = args.length;
		final String[] strings = new String[argsLength];
		for (int i = 0; i < argsLength; i++) {
			String arg = args[i];
			final Path hoconFileToRead = Path.of(arg);
			if (!Files.exists(hoconFileToRead)) {
				System.err.println("File \"" + hoconFileToRead + "\" does not exist");
				System.exit(1);
				return;
			}

			try {
				final CommentedConfigurationNode hocon = hoconBuilder
						.path(hoconFileToRead)
						.build()
						.load();

				final String json = jacksonBuilder
						.buildAndSaveString(hocon);

				strings[i] = json;
			} catch (ConfigurateException e) {
				System.err.println("Error trying to read file \"" + hoconFileToRead + "\":");
				throw new RuntimeException(e);
			}
		}

		System.out.print(String.join("\0", strings));
	}
}
