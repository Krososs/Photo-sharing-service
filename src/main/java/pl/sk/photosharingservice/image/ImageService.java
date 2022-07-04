package pl.sk.photosharingservice.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.sk.photosharingservice.appUser.appUserService;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final pl.sk.photosharingservice.appUser.appUserService appUserService;

    @Autowired
    public ImageService(ImageRepository imageRepository, appUserService appUserService) {
        this.imageRepository = imageRepository;
        this.appUserService = appUserService;
    }

    public void UploadImage(MultipartFile file, String username, String description) {

        Image image = new Image(appUserService.getUserIdByUsername(username), file.getOriginalFilename(), description);
        try {
            image.setSource(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageRepository.save(image);
    }

    public Image getImageById(Long imageId) {
        return imageRepository.getById(imageId);
    }

    public boolean imageExists(Long imageId) {
        return imageRepository.existsById(imageId);
    }

    public boolean userIsOwner(Long imageId, String username) {
        Long imageOwnerId = imageRepository.getById(imageId).getOwnerId();
        Long userId = appUserService.getUserIdByUsername(username);
        return imageOwnerId.equals(userId);
    }

    public void deleteImageById(Long imageId) {
        imageRepository.deleteById(imageId);
    }

    public boolean checkIfLiked(Long userId, Long ImageId) {
        return imageRepository.checkIfLiked(userId, ImageId).isPresent();
    }

    public List<Image> getImagesById(Long id) {
        return imageRepository.findByOwnerId(id);
    }

}
