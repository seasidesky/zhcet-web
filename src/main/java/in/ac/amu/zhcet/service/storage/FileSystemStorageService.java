package in.ac.amu.zhcet.service.storage;

import in.ac.amu.zhcet.service.user.Auditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootCsvLocation;
    private final Path rootImgLocation;
    private final Path rootDocLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootCsvLocation = Paths.get(properties.getCsv().getLocation());
        this.rootImgLocation = Paths.get(properties.getImage().getLocation());
        this.rootDocLocation = Paths.get(properties.getDocument().getLocation());
    }

    private Path fromFileType(FileType fileType) {
        switch (fileType) {
            case CSV:
                return rootCsvLocation;
            case IMAGE:
                return rootImgLocation;
            case DOCUMENT:
                return rootDocLocation;
            default:
                return rootDocLocation;
        }
    }

    @Override
    public String generateFileName(String name) {
        return StringUtils.cleanPath(LocalDateTime.now().toString() + "_" + Auditor.getLoggedInUsername() + "_" + name);
    }

    private void storeAbstract(FileType fileType, String name, boolean empty, InputStream inputStream) {
        String filename = generateFileName(name);
        try {
            if (empty) {
                throw new StorageException("Failed to store empty file " + filename);
            }

            if (filename.contains("..")) {
                String message = "Cannot store file with relative path outside current directory " + filename;
                // This is a security check
                log.warn(message);
                throw new StorageException(message);
            }
            Files.copy(inputStream, fromFileType(fileType).resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            log.info("Saved file " + filename);
        } catch (IOException e) {
            log.error(String.format("Failed storing file %s", filename), e);
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public void store(FileType fileType, File file) {
        try {
            storeAbstract(fileType, file.getName(), file.length() == 0, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error(String.format("Failed storing file %s", file.getName()), e);
            throw new StorageException("Failed to store file " + file.getName(), e);
        }
    }

    @Override
    public void store(FileType fileType, MultipartFile file) {
        try {
            storeAbstract(fileType, file.getOriginalFilename(), file.isEmpty(), file.getInputStream());
        } catch (IOException e) {
            log.error(String.format("Failed storing file %s", file.getName()), e);
            throw new StorageException("Failed to store file " + file.getName(), e);
        }
    }

    @Override
    public Stream<Path> loadAll(FileType fileType) {
        try {
            Path location = fromFileType(fileType);
            return Files.walk(location, 1)
                    .filter(path -> !path.equals(location))
                    .map(location::relativize);
        }
        catch (IOException e) {
            log.error("Failed to read files", e);
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(FileType fileType, String filename) {
        return fromFileType(fileType).resolve(filename);
    }

    @Override
    public Resource loadAsResource(FileType fileType, String filename) {
        try {
            Path file = load(fileType, filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                log.error("Failed to read file {}", filename);
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            log.error(String.format("Failed to read file %s", filename), e);
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll(FileType fileType) {
        FileSystemUtils.deleteRecursively(fromFileType(fileType).toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootCsvLocation);
            Files.createDirectories(rootImgLocation);
            Files.createDirectories(rootDocLocation);
        } catch (IOException e) {
            log.error("Could not initialize storage", e);
            throw new StorageException("Could not initialize storage", e);
        }
    }
}