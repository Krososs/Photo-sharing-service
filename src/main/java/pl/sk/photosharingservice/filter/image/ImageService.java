package pl.sk.photosharingservice.filter.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.sk.photosharingservice.appUser.appUserService;
import pl.sk.photosharingservice.support.AuthUtil;

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

    public void UploadImage(MultipartFile file, String username, String description){

        Image image = new Image(appUserService.getUserIdByUsername(username), file.getOriginalFilename().toString(), description);
        try {
            image.setSource(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageRepository.save(image);
    }

    public boolean deleteImage(Long imageId, String token){

        //check that user is the owner of the image
        Image image = imageRepository.getById(imageId);
        Long id = appUserService.getUserIdByUsername(AuthUtil.getUsernameFromToken(token));

        if(image.getOwnerId().equals(id)) {
            imageRepository.deleteImageById(imageId);
            return true;
        }
        else
            return false;
    }

    public boolean checkIfLiked(Long userId, Long ImageId){
        return imageRepository.checkIfLiked(userId, ImageId).isPresent();
    }

    public List<Image> getImagesById(Long id){
        return imageRepository.findByOwnerId(id);
    }


}
