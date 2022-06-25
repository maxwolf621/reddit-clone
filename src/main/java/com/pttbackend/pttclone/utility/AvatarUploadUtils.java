package com.pttbackend.pttclone.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.pttbackend.pttclone.exceptions.StorageException;

import lombok.experimental.UtilityClass;

import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Utility Class for uploading user avatar 
 * @see <a href="https://www.codejava.net/frameworks/spring-boot/spring-boot-file-upload-tutorial">
 *      source code </a>
 */
@UtilityClass
public class AvatarUploadUtils {

    /**
     * <p> Resolve rootLocation with uploadedFilename via {@code Path#resolve(Path)} </p>
     * <p> Download the file that is uploaded by client to source via {@code clientUploadedFile} </p> 
     * @param rootLocation save the uploaded file to here "userPhotos/" + savedUser.getId();
     * @param uploadedFilename StringUtils.cleanPath(MultipartFile#getOriginalFilename())
     * @param clientUploadedFile Bytes (date of client's uploaded files)
     */
    public static void saveFile(String rootLocation, String uploadedFilename, MultipartFile clientUploadedFile){
        
        // Covert String type path to Path Object
        Path fileSavingPath =  Paths.get(rootLocation);
 
        try{
            if(!Files.exists(fileSavingPath)){
                Files.createDirectories(fileSavingPath);
            }
        }
        catch(IOException e){
            throw new StorageException("Could not initialize storage", e);
        }

        // rootLocation/uploadFileName 
        // e.g. 
        // home/Desktop/sprinbootapplication/rootlocation/uploadfilename
        Path destinationFile = fileSavingPath.resolve(uploadedFilename);

        try(InputStream inputStream = clientUploadedFile.getInputStream())
        {
            /**
             * download the data of the uploaded file (@code inputStream) to
             * {@code destinationFile} 
             */
            Files.copy(inputStream, destinationFile,StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            throw new StorageException("Cannot store file outside current directory");
        }
    }

    /**
     * Delete the image of the user
     * @param rootLocation storage where stores user's avatar 
     */
    public static void deleteAll(Path rootLocation) {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}
}
