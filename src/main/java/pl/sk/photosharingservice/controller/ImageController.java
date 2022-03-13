package pl.sk.photosharingservice.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.sk.photosharingservice.model.Image;
import pl.sk.photosharingservice.model.User;
import pl.sk.photosharingservice.service.ImageService;

import java.util.List;

@RestController
public class ImageController {

    private final ImageService imageService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/images/upload")
    public ResponseEntity uploadImage(@RequestBody MultipartFile file, @RequestParam String ownerId)
    {

        //Long id = Long.valueOf(ownerId).longValue();
        //System.out.println(id);
        //System.out.println(id.getClass());
        //System.out.println(description);
        imageService.Upload(file, Long.valueOf(ownerId).longValue());
        return ResponseEntity.ok("Jest git");
    }


    @GetMapping("/images")
    public ResponseEntity getImages() {
        List<Image> images= imageService.getAllImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/images/user")
    public ResponseEntity getImagesById(@RequestParam String ownerId) {
        List<Image> images= imageService.getImagesById(Long.valueOf(ownerId).longValue());
        return ResponseEntity.ok(images);
    }


}
