package com.blog.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.services.FileService;
@Service
public class FileServiceImpl implements FileService{

	@Override
	public String uploadImage(String path, MultipartFile file) throws IOException {
		//filename
		String name = file.getOriginalFilename();
		Stream<Path> stream = Files.list(Paths.get("images/"));
		List<String> rs = stream
		          .filter(fil -> !Files.isDirectory(fil))
		          .map(Path::getFileName)
		          .map(Path::toString)
		          .collect(Collectors.toList());
		for(int i=0;i<rs.size();i++) {
			if((name.toLowerCase()).equalsIgnoreCase(rs.get(i))) {
				name = name+"(1)";
			}
		}
		
		//random generate filename
//		String randomID = UUID.randomUUID().toString();
//		String fileName1=randomID.concat(name.substring(name.lastIndexOf(".")));
		//full path
		String filePath = path+File.separator+name;

		//create folder if not created
		File f = new File(path);
		if(!f.exists()) {
			f.mkdir();
		}
		//file copy
		Files.copy(file.getInputStream(),Paths.get(filePath));
		return name;
	}

	@Override
	public InputStream getResource(String path, String fileName) throws FileNotFoundException {
		String filePath = path+File.separator+fileName;
		InputStream is = new FileInputStream(filePath);
		return is;
	}

}
