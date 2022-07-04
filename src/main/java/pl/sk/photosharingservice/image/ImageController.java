package pl.sk.photosharingservice.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static pl.sk.photosharingservice.support.ResponseUtil.*;
import static pl.sk.photosharingservice.support.ValidationUtil.IMAGE_MAX_SIZE;


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
    public ResponseEntity<?> uploadImage(@RequestPart MultipartFile file, @RequestPart String description, @RequestHeader("language") String language, @RequestHeader("authorization") String token) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Language l = (Language) Class.forName("pl.sk.photosharingservice.support.language." + language).newInstance();

        if (file.getSize() > IMAGE_MAX_SIZE)
            return new ResponseEntity<>(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), IMAGE_SIZE_TOO_LARGE.translate(l)), HttpStatus.CONFLICT);
        if (description != null) {
            if (description.length() > ValidationUtil.IMAGE_DESCRIPTION_MAX_LENGTH)
                return new ResponseEntity<>(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), IMAGE_DESCRIPTION_TOO_LONG.translate(l)), HttpStatus.CONFLICT);
        }
        imageService.UploadImage(file, AuthUtil.getUsernameFromToken(token), description);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/images/like")
    public ResponseEntity<?> likeImage(@RequestParam Long imageId, @RequestHeader("language") String language, @RequestHeader("authorization") String token) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Language l = (Language) Class.forName("pl.sk.photosharingservice.support.language." + language).newInstance();

        if (!imageService.imageExists(imageId))
            return new ResponseEntity<>(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), IMAGE_DOES_NOT_EXISTS.translate(l)), HttpStatus.CONFLICT);

        likeService.handleLike(appUserService.getUserIdByUsername(AuthUtil.getUsernameFromToken(token)), imageId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/images/image/likes")
    public ResponseEntity<?> getUsersWhoLiked(@RequestParam Long imageId, @RequestHeader("language") String language, @RequestHeader("authorization") String token) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Language l = (Language) Class.forName("pl.sk.photosharingservice.support.language." + language).newInstance();

        if (!imageService.imageExists(imageId))
            return new ResponseEntity<>(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), IMAGE_DOES_NOT_EXISTS.translate(l)), HttpStatus.CONFLICT);

        ObjectNode imageInfo = new ObjectMapper().createObjectNode();
        ArrayNode _users = imageInfo.putArray("users");

        List<appUser> users = likeService.getUsersWhoLike(imageId);

        boolean liked = users.stream()
                .anyMatch(o -> o.getUsername().equals(AuthUtil.getUsernameFromToken(token)));

        imageInfo.put("liked", liked);

        for (appUser u : users)
            _users.add(u.toJson());

        return new ResponseEntity<>(imageInfo, HttpStatus.OK);
    }

    @DeleteMapping("/images/delete")
    public ResponseEntity<?> deleteImage(@RequestParam Long imageId, @RequestHeader("language") String language, @RequestHeader("authorization") String token) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Language l = (Language) Class.forName("pl.sk.photosharingservice.support.language." + language).newInstance();
        String username = AuthUtil.getUsernameFromToken(token);

        if (!imageService.imageExists(imageId))
            return new ResponseEntity<>(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), IMAGE_DOES_NOT_EXISTS.translate(l)), HttpStatus.CONFLICT);
        if (!imageService.userIsOwner(imageId, username))
            return new ResponseEntity<>(ValidationUtil.getErrorResponse(HttpStatus.CONFLICT.value(), USER_IS_NOT_RIGHT_OWNER.translate(l)), HttpStatus.CONFLICT);

        imageService.deleteImageById(imageId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/images/user")
    public ResponseEntity<?> getImagesById(@RequestParam Long ownerId) {
        List<Image> images = imageService.getImagesById(ownerId);
        return new ResponseEntity<>(images, HttpStatus.OK);
    }
}
