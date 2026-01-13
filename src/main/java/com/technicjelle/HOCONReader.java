package com.technicjelle;

import com.jayway.jsonpath.JsonPath;
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
	public static void main(String[] args) throws ConfigurateException {
		if (args.length == 0) {
			System.err.println("Provide a file to read. It will be converted to JSON and printed to stdout.");
			System.err.println("Optionally provide a JSON path to extract stuff from the JSON output.");
			System.exit(1);
			return;
		}

		final Path hoconFileToRead = Path.of(args[0]);
		if (!Files.exists(hoconFileToRead)) {
			System.err.println("File \"" + hoconFileToRead + "\" does not exist");
			System.exit(1);
			return;
		}

		final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
				.defaultNamingScheme(NamingSchemes.PASSTHROUGH)
				.build();

		final CommentedConfigurationNode hocon = HoconConfigurationLoader.builder()
				.defaultOptions(opts -> opts.serializers(build -> build.registerAnnotatedObjects(customFactory)))
				.path(hoconFileToRead)
				.build()
				.load();

		final String json = JacksonConfigurationLoader.builder()
				.headerMode(HeaderMode.NONE)
				.buildAndSaveString(hocon);

		if (args.length == 2) {
			final String jsonPath = args[1];
			final String filtered = JsonPath.read(json, jsonPath);
			System.out.println(filtered);
		} else {
			System.out.println(json);
		}
	}
}
