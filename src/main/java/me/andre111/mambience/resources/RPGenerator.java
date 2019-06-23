package me.andre111.mambience.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RPGenerator {
	// run with
	// java -cp Mambience-[version].jar me.andre111.mambience.resources.RPGenerator
	// to generate/extract the resourcepack
	public static void main(String[] args) {
		try {
			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			Path path = Paths.get("./mambience_resources.zip");
			Files.deleteIfExists(path);
			URI uri = URI.create("jar:" + path.toUri());

			try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
				// create pack.mcmeta
				transferFile("/pack_template.mcmeta", fs.getPath("/pack.mcmeta"));

				// iterate all asset directories and transfer files
				Set<String> filePaths = getResourceFiles("assets/");
				for(String filePath : filePaths) {
					transferFile("/"+filePath, fs.getPath("/"+filePath));
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static Set<String> getResourceFiles(String startPath) throws IOException {
		// find location of own class file
		String me = RPGenerator.class.getName().replace(".", "/") + ".class";
		URL dirURL = RPGenerator.class.getClassLoader().getResource(me);

		if (dirURL.getProtocol().equals("jar")) {
			// strip out only the JAR file
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
			try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {

				// gives ALL entries in jar
				Enumeration<JarEntry> entries = jar.entries();
				Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (name.startsWith(startPath)) { // filter according to the path
						result.add(name);
					}
				}
				return result;
			}
		} else {
			throw new UnsupportedOperationException("Can only extract resources from jar file!");
		}
	}

	private static void transferFile(String sourcePath, Path targetPath) throws IOException {
		if(Files.isDirectory(targetPath)) return;
		
		try (InputStream is = RPGenerator.class.getResourceAsStream(sourcePath)) {
			Files.createDirectories(targetPath.getParent());
			Files.copy(is, targetPath);
		}
	}
}