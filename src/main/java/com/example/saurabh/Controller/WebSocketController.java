package com.example.saurabh.Controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.saurabh.entity.Message;

@Controller
public class WebSocketController {
	
	  String mechanicName;
	
	
	

    //initial friend request
    @MessageMapping("/message")
    @SendTo("/topic/response")
    public Message handleFriendRequest(Message message) {
     
        System.out.println("Friend request message received: " + message);
        
        
        return  message;
    }
    
    
    
  //========================================================================================
	
    //issi notification me backend se ek name aur message aa rha that 
  	//your this driver accepted your request to show to user and in this message there is a name of mechanic 
  	//which we extract here aur uss naam ko class level variable me show kar rhe taaki issi naam ke according
  	//getNearestMechanic ko uss nam k accoding findbyname se wo mechanic object bhej sake jo aage ja k map me 
  	//show hoga user ki location of confirm kiya hua driver 
  	//pehle login kiya loginsuccess hote ek model me uss mechanic ko save kar k bheja mechanic home page par 
  	//wha uss model me se mechanic attribute se mechanic ka naam nikal k welcom mechanic xx likhwaya 
  	//fir wahi mechanic object ko js me le gaye yhi meechanic obj se naam nikala mesage me dala bass 
  	//fir yahi msg se naam nikaal rhe yaha aur bhej bhi rhe 
    
    
  	
    

    //accept/reject notification
    @MessageMapping("/notification")
    @SendTo("/topic/getnoti")
    public Message handleNotification(Message message) {
    this.mechanicName=	message.getName();
      
        
        return message;
    }
    
    
    
}
