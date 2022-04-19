package pl.sk.photosharingservice.filter.image;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.sk.photosharingservice.appUser.appUser;
import pl.sk.photosharingservice.like.LikeService;
import pl.sk.photosharingservice.appUser.appUserService;
import pl.sk.photosharingservice.support.AuthUtil;
import pl.sk.photosharingservice.support.ValidationUtil;
import pl.sk.photosharingservice.support.language.Language;
import java.util.List;
import java.util.stream.Collectors;

import static pl.sk.photosharingservice.support.ResponseUtil.IMAGE_DESCRIPTION_TOO_LONG;


@RestController
public class ImageController {

    private final ImageService imageService;
    private final LikeService likeService;
    private final appUserService appUserService;

    @Autowired
    public ImageController(ImageService imageService, LikeService likeService, appUserService appUserService) {
        this.imageService = imageService;
        this.likeService = likeService;
        this.appUserService = appUserService;
    }

    @PostMapping("/images/upload")
    public ResponseEntity<?> uploadImage(@RequestPart MultipartFile file ,@RequestPart String description,@RequestHeader("language") String language, @RequestHeader("authorization") String token) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Language l =  (Language)Class.forName("pl.sk.photosharingservice.support.language."+language).newInstance();

        if(description!=null) {
            if (description.length() > ValidationUtil.IMAGE_DESCRIPTION_MAX_LENGTH) {
                JSONObject error = new JSONObject();
                error.put("error", IMAGE_DESCRIPTION_TOO_LONG.translate(l));
                return new ResponseEntity<>(error.toMap(), HttpStatus.CONFLICT);
            }
        }
        imageService.UploadImage(file, AuthUtil.getUsernameFromToken(token), description);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @PostMapping("/images/like")
    public ResponseEntity<?> likeImage(@RequestParam String imageId, @RequestHeader("authorization") String token)
    {

        likeService.handleLike(appUserService.getUserIdByUsername(AuthUtil.getUsernameFromToken(token)),Long.valueOf(imageId));
        return new ResponseEntity<>( HttpStatus.OK);
    }

    //get people who liked
    @GetMapping("/images/image/likes")
    public ResponseEntity<?> getUsersWhoLiked(@RequestParam String imageId, @RequestHeader("authorization") String token)
    {
        JSONObject imageData = new JSONObject();
        List<appUser> users =  likeService.getUsersWhoLike(Long.valueOf(imageId));

        boolean liked = users.stream()
                .filter(o -> o.getUsername().equals(AuthUtil.getUsernameFromToken(token)))
                .findFirst().isPresent();

        imageData.put("users", users.stream().map(appUser -> appUser.toJson()).collect(Collectors.toList()));
        imageData.put("liked", liked);
        return ResponseEntity.ok(imageData.toMap());

    }


    @DeleteMapping("/images/delete")
    public ResponseEntity<?> deleteImage(@RequestParam String imageId, @RequestHeader("authorization") String token)
    {
        System.out.println(imageId);
        if(imageService.deleteImage(Long.valueOf(imageId), token)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        else return new ResponseEntity(HttpStatus.CONFLICT);
    }

    @GetMapping("/images/user")
    public ResponseEntity<?> getImagesById(@RequestParam String ownerId) {
        List<Image> images= imageService.getImagesById(Long.valueOf(ownerId));
        return ResponseEntity.ok(images);
    }


}
