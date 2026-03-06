package com.technicjelle;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.util.NamingSchemes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

public class HOCONReader {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Provide HOCON file(s) to read. It/they will be converted to JSON and printed to stdout.");
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
			} catch (ParsingException e) {
				int line = e.line() + getHeaderLength(hoconFileToRead);
				String errorMessage = e.getCause().getMessage();
				errorMessage = errorMessage.replaceFirst("Reader: \\d+: ", "");
				strings[i] = "Error trying to parse file \"" + hoconFileToRead + "\":\n" +
							 "Line: " + line + "\n" +
							 errorMessage + "\n";
			} catch (ConfigurateException e) {
				strings[i] = "Error trying to read file \"" + hoconFileToRead + "\":\n"
							 + e.rawMessage() + "\n";
			}
		}

		System.out.print(String.join("\0", strings));
	}

	private static int getHeaderLength(Path file) {
		try (final Stream<String> lines = Files.lines(file)) {
			final Iterator<String> iterator = lines.iterator();

			int headerLength = 0;
			while (iterator.hasNext()) {
				String line = iterator.next();
				if (line.startsWith("#") || line.startsWith("//")) {
					// Count commented lines from the start
					headerLength++;
				} else if (line.isBlank()) {
					// If we encounter a blank line, that is the end of the header
					headerLength++; // (Also count this blank line)
					break;
				} else {
					// If we encounter a line that is not commented, but also not blank, that means we encountered an actual key,
					// which means that what we have been counting here was actually NOT a header, but a key-comment.
					// This file does not actually have a header after all, so we reset to 0 and break.
					headerLength = 0;
					break;
				}
			}
			return headerLength;
		} catch (IOException e) {
			// If something went wrong, we just return 0.
			// We'll just use the ParsingException's own line number.
			// It is likely that that one is incorrect, but it's not THAT bad if the line number is a bit off.
			// Better than crashing the program while trying to log an error, at least... :^)
			return 0;
		}
	}
}
