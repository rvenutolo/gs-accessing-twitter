package hello;

import javax.inject.Inject;

import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HelloController {
	
    private Twitter twitter;
    
    @Inject
    public HelloController(Twitter twitter) {
        this.twitter = twitter;		
    }
    
    @RequestMapping(method=RequestMethod.GET)
    public String helloTwitter(Model model) {
        if (!twitter.isAuthorized()) {
            return "redirect:/connect/twitter";
        }
        
        model.addAttribute(twitter.userOperations().getUserProfile());
        CursoredList<TwitterProfile> friends = twitter.friendOperations().getFriends();
        model.addAttribute("friends", friends);
        return "hello";
    }
	
}
