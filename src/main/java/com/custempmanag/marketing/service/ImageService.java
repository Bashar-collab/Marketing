package com.custempmanag.marketing.service;

import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.exception.DenyAccessException;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Image;
import com.custempmanag.marketing.model.Imageable;
import com.custempmanag.marketing.model.Offering;
import com.custempmanag.marketing.repository.ImageRepository;
import com.custempmanag.marketing.repository.OfferingRepository;
import com.custempmanag.marketing.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    private final UserService userService;
    private final OfferingRepository offeringRepository;

    @Autowired
    private MessageSource messageSource;

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    public void savePhoto(Imageable entity, MultipartFile photo, String relativeDir) {
        try {
//            String relativeDir = "offering";
            Path targetDir = Paths.get(fileUploadDir, relativeDir);

            Files.createDirectories(targetDir);

            String filename = UUID.randomUUID() + "_" +
                    (photo.getOriginalFilename() != null ?
                            photo.getOriginalFilename() : "uploaded_file");

            Path filePath = targetDir.resolve(filename);
            photo.transferTo(filePath.toFile());

//            return filePath.toString();
            String imagePath = Paths.get(relativeDir, filename).toString().replace("\\", "/");

            Image image = new Image();
            image.setImageableId(entity.getId());
            image.setImageableType(entity.getClass().getSimpleName().toLowerCase());
            image.setPath(imagePath);
            imageRepository.save(image);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new CustomException("Failed to upload offering picture, please try again");
        }
    }

    public String getImagePath(Image image) {
        String imagePath = image.getPath();
        String fileName = Paths.get(imagePath).getFileName().toString();
        String folder = image.getImageableType().toLowerCase();

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return baseUrl + "/api/files/" + folder + "/" + fileName;
    }

    public MessageResponse deleteImage(Long imageId) {

    imageRepository.deleteById(imageId);

    return new MessageResponse(HttpStatus.OK.toString(),
                                messageSource.getMessage("image.delete.success", null, LocaleContextHolder.getLocale()),
                                null);
}


}
