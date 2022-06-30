package pl.sk.photosharingservice.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.sk.photosharingservice.appUser.appUser;
import pl.sk.photosharingservice.appUser.appUserService;
import pl.sk.photosharingservice.image.ImageRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final ImageRepository imageRepository;
    private final pl.sk.photosharingservice.appUser.appUserService appUserService;


    @Autowired
    public LikeService(LikeRepository likeRepository, ImageRepository imageRepository, appUserService appUserService) {
        this.likeRepository = likeRepository;
        this.imageRepository = imageRepository;
        this.appUserService = appUserService;
    }

    public void handleLike(Long userId, Long imageId){

        if(imageRepository.findById(imageId).isPresent()){

            if(!imageRepository.checkIfLiked(userId, imageId).isPresent())
                likeRepository.save(new Like(userId, imageId));
            else
                likeRepository.Unlike(userId, imageId);
        }

    }

    //get users who like image with present id
    public List<appUser> getUsersWhoLike(Long imageId){
        
        List<Like> likes = likeRepository.findByImageId(imageId);
        List<appUser> users = new ArrayList<>();

        likes.forEach(like -> {
            if(appUserService.getUserById(like.getUserId()).isPresent())
                users.add(appUserService.getUserById(like.getUserId()).get());
        });

        return users;
    }

    public boolean checkIfLiked(){
        return true;
    }

}
