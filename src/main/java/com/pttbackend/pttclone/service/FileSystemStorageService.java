package com.pttbackend.pttclone.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.pttbackend.pttclone.config.StorageProperties;
import com.pttbackend.pttclone.exceptions.StorageException;
import com.pttbackend.pttclone.exceptions.StorageFileNotFoundException;
import com.pttbackend.pttclone.interfaces.StorageService;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;


import lombok.extern.slf4j.Slf4j;

/**
 * Client uploads his file to Server
 * @see <a href="https://www.baeldung.com/java-nio-2-path"> 
 *      usage of nio.file.Paths </a>
 */
@Service
@Slf4j
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;
	
	@Autowired
	private AuthenticationService authService;
	
	@Autowired
	private UserRepository userRepo;
	
	/**
	 * <p> Get our root Location from environment variable </p>
	 * <pre> path = Paths.get(URI object); </pre>
	 * Each Directory is the Element e.g. C:\\element1\element2\element3\filename
	 * @param properties get parent directory of uploaded file 
	 */
	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
		log.info("----------Root Location : "+ rootLocation);
	}

	/**
	 * 
	 * @see <a href="https://www.baeldung.com/java-nio-2-path#normalizing-a-path"> normalizing the path of the uploaded file </a>
	 * @see <a href="https://www.baeldung.com/java-nio-2-path#path-conversion"> path conversion .toAbsolutePath() </a>
	 */ 
	@Override
	public void store(MultipartFile file) {
		
		User user = authService.getCurrentUser();

		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file that Uploaded by Client.");
			}
			// Resolve the given path (client's uploaded file) against this path (root location).
			Path destinationFile = this.rootLocation.resolve(
					/**
					 * Get the original filename in the client's filesystem.
					 * Convert a path string, or a sequence of strings that when joined form a path string (client side), to a Path (object)
					 */
					Paths.get(file.getOriginalFilename()))
					/**
					  * Normalize the given path that is with redundant name elements (e.g. /../xxx, or /././xxx) eliminated.
					  */  
					.normalize()
					/** 
					 * representing the absolute path of this given normalized path.
					 */
					.toAbsolutePath();
					
					log.info("----------file "+ file.getOriginalFilename());
					log.info("----------destinationFile "+ destinationFile.toAbsolutePath().toString());
					
					// Check the uploaded file stored in right directory(parent)
					if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
						
						log.warn(destinationFile.getParent().toString()+" and "+rootLocation.toAbsolutePath().toString());
						
						throw new StorageException(
							"Cannot store file outside current directory (" 
							+ this.rootLocation.toAbsolutePath() 
							+ ")");
					}

			
			user.setAvatar(destinationFile.getFileName().toString());
			userRepo.save(user);
			
			// Download the uploaded file's date to the Server's Physical Storage Space
			try (InputStream inputStream = file.getInputStream()) {
					/**
					 * Copies all bytes from an input stream to a file (object {@code Files}). 
					 * On return, the input stream will be at end of stream.
					 */
					Files.copy(inputStream, destinationFile,StandardCopyOption.REPLACE_EXISTING);
				}
		}catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			// Each file is traversed depth-first
			return Files.walk(this.rootLocation, 1)
						.filter(path -> !path.equals(this.rootLocation))
						.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@PostConstruct
	@Override
	public void init() {
		try {
			//if(!Files.exists(rootLocation)){
				log.info("**** Initialize The FileSystem *****");
				Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
