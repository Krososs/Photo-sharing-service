package pl.sk.photosharingservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.sk.photosharingservice.model.Image;
import pl.sk.photosharingservice.model.User;
import pl.sk.photosharingservice.repository.ImageRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class ImageService {

    private final ImageRepository imageRepository;


    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }


    public void Upload(MultipartFile file, Long ownerId){
        Image image = new Image(ownerId, file.getOriginalFilename().toString());
        try {
            image.setSource(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageRepository.save(image);
        //System.out.println("Pomyślnie zapisano zdjęcie do bazy");
    }

    public Long getUserPhotosCount(Long ownerId ){

        return imageRepository.countByOwnerId(ownerId);
    }

    public List<Image> getAllImages(){
        return imageRepository.findAll();
    }
    public List<Image> getImagesById(Long id){
        return imageRepository.findByOwnerId(id);
    }

}
