package com.example.saurabh.Controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.saurabh.entity.Mechanic;
import com.example.saurabh.entity.MechanicRepository;

@Controller
public class LocationController {
	    
	 // ye isliye ki jo mechanic accept kiya hai uska naam webshocket kke controller me 
	// public variable me sve hua aur dusre controll ki cheeze dusre controler me nahi use kar sakte 
	//isiliye webSocketcontroller  ko yaha autowired kar diye 
	
	@Autowired
    private WebSocketController controller1;

	
	 @Autowired
	    private MechanicRepository mecaRepository;

	@GetMapping("/show-location")
	public String showLocation() {
		
		//ye return karega simple html file jisme sare mechanic dikhenge
		return "AllLocFile"; 
	}
    //phle hum log yahi location manually de rhe the frontend ko jaiesa ki normal book file me dekh sakte hai
	//iske upar wale method me model as prameter pass karte the fir model.addatrribute =>
	// model.addAttribute("latitude1", latitude1);
	//model.addAttribute("longitude1", longitude1);aiese bhejte the backend se manually
	//aur frontend me kai var me store karte the fetch karte the  var Driver1Lat = [[${latitude1}]];// driver1 location
   //getmechnics ab backend se data bhejega front end ko database me se nilkaal ke
	//ye niche wala meyhod bhejega list me user ka data jo ki json me jayega
	//aur frontend fetch('/getMechanics').then(response => response.json()) se data fetch karega 
	
	@GetMapping("/getMechanics")
    @ResponseBody
    public List<Mechanic> getMechanics() {
        return  mecaRepository.findAll();
      
    }
	 
	@GetMapping("/getNearestMechanic")
	@ResponseBody
	public Mechanic getsinglemecname() {
		    
		   Mechanic mechanic = mecaRepository.findByName(controller1.mechanicName);
		return mechanic;
		
	}
	
	
		
   //===============================================================================================
	
	
	
	
	//===============================================
	
	//ab mechnaic page ke liye direction denge 
	
	
	 // Show form for adding a mechanic(bas samjhne ke liye hai niche wala)
	//ye emty model bhejega aur usme ek key "mechanic" aurvalue me mechanic khali object from mechanic entity se
	
    @GetMapping("/addMechanic") 
    public String showAddForm(Model model) {
        model.addAttribute("mechanic", new Mechanic());
        
        return "AddMechanic";
        
    }
  //jioo ki object ka har paramter ek input box se connect ho jayega aur waha se la ke database me save kar dega like
  	// mechnaic m = new mechanic(); fir m.setlatitude kar rhe the manually deatil set kar rhe the
    //yaha form se db me save kar rhe db
    
    
    @PostMapping("/addMechanic")
    public String addMechanic(@ModelAttribute Mechanic mechanic,
                              @RequestParam("file") MultipartFile file) throws IOException {
        
    	byte[] bytes = file.getBytes();
        String base64String = Base64.getEncoder().encodeToString(bytes);
        mechanic.setPhotoBase64(base64String);
        mecaRepository.save(mechanic);
        return "redirect:/loginmec";
    }

    
    //for login page 
    @GetMapping("/loginmec")
    public String showLoginForm(Model model) {
        model.addAttribute("login", new Mechanic()); //khaali mechanic jo ja k chhipak jayega
        return "logy"; 
    }

    
    @PostMapping("/loginmec")
    public String handleLogin(@ModelAttribute("login") Mechanic mechanic, Model model) {
        //databse se mechanic object uthaa ke laa rhe 
        Mechanic existingMechanic = mecaRepository.findByName(mechanic.getName());
        
        if (existingMechanic != null && existingMechanic.getPassword().equals(mechanic.getPassword())) {
            // Login success
        	model.addAttribute("mechanic", existingMechanic);
            return "Second"; 
        } else {
            // Login failed
            model.addAttribute("error", "Invalid username or password");
            return "logy";
        }
    }

    @PostMapping("/paymentSuccess")
    public ResponseEntity<String> paymentSuccess(@RequestParam String paymentId) {
        System.out.println("Payment Successful: " + paymentId);
        return ResponseEntity.ok("Payment Received Successfully!");
    }

    

    @GetMapping("/sec")
	public String Showhomee(){
		return "Second";
		
	}
    
    @GetMapping("/loginuser")
    public String loguser() {
    	return "ulo";
    }
 
	@GetMapping("/chat")
	public String chatwithme(){
		return "chating";
	}
	
	@GetMapping("/chat2")
	public String chatwithm(){
		return "chating2";
	}
	
	@GetMapping("/")
	public String Showhome() {
		return "index";
		
	}
	@GetMapping("/booked")
	public String Show() {
		return "CBook";
		
	}
	
	
	
	
	
	@PostMapping("/toggleStatus")
	@ResponseBody
	public ResponseEntity<String> toggleStatus(@RequestParam String name, @RequestParam String status) {
	    Mechanic mechanic = mecaRepository.findByName(name);
	    System.out.println("Name: " + name + ", Status: " + status);
	    if (mechanic != null) {
	        mechanic.setStatus(status);
	        mecaRepository.save(mechanic);
	        return ResponseEntity.ok("Status updated to " + status);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mechanic not found");
	    }
	}

	


	@PostMapping("/delete")
	public String deleteMechanic(@RequestParam String name, Model model) {
	    Mechanic mechanic = mecaRepository.findByName(name);
	    if (mechanic != null) {
	        mecaRepository.delete(mechanic);
	        model.addAttribute("message", "Mechanic '" + name + "' deleted successfully.");
	    } else {
	        model.addAttribute("message", "Mechanic with name '" + name + "' not found.");
	    }
	    // Refresh list after delete
	    model.addAttribute("mechanics", mecaRepository.findAll());
	    return "adminMechanicsPage";
	}
	

	@GetMapping("/getAvailableMechanics")
	@ResponseBody
	public List<Mechanic> getAvailableMechanics() {
	    return mecaRepository.findByStatus("Available");
	}

	@GetMapping("/searchById")
	@ResponseBody
	public ResponseEntity<?> searchById(@RequestParam Long id) {
	    Optional<Mechanic> mechanicOpt = mecaRepository.findById(id);
	    if (mechanicOpt.isPresent()) {
	        return ResponseEntity.ok(mechanicOpt.get());
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mechanic not found");
	    }
	}

	
	
	

   
	
	

	
	@GetMapping("/admin")
	public String getMethodName() {
		return "adminMechanicsPage";
	}
	
	
	
	
	
	
}






