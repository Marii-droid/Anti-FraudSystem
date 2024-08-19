package antifraud;


import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse registerUser(@Valid @RequestBody UserRequest userRequest) throws UserAlreadyExistsException {
        if (userRepository.existsByUsername(userRequest.username())) {
            throw new UserAlreadyExistsException();
        }

        User user = userRequest.requestToUser(passwordEncoder);
        user = userRepository.save(user);
        return new UserResponse(user);
    }

   @GetMapping("/list")
   List<UserResponse> getAllUsers() {
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : userRepository.findByOrderById()) {
            userResponses.add(new UserResponse(user));
        }
        return userResponses;
   }

    @DeleteMapping("/user/{username}")
    @Transactional
    DeleteUserResponse deleteUser(@PathVariable("username") String username, @AuthenticationPrincipal UserDetails deleter) throws UserNotFoundException{ //username als paramenter toevoegen anders kan die kwijtgeraakt worden
        if (!userRepository.existsByUsername(username)){
            throw new UserNotFoundException();
        }
        userRepository.deleteByUsername(username);
        return new DeleteUserResponse(username);
        }


    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus (HttpStatus.CONFLICT)
    void handleUserAlreadyExistsException(UserAlreadyExistsException e) {}

    @ExceptionHandler (UserNotFoundException.class)
    @ResponseStatus (HttpStatus.NOT_FOUND)
    void handleUserNotFoundException(UserNotFoundException e) {}

    record UserRequest(@NotBlank String name, @NotBlank String username, @NotBlank String password) {
        User requestToUser(PasswordEncoder passwordEncoder) {
            User user = new User();
            user.setName(this.name());
            user.setUsername(this.username());
            user.setPassword(passwordEncoder.encode(this.password()));
            return user;
        }
    } //valid checkpoint
    record UserResponse(long id, String name, String username) {
        UserResponse (User user) {
            this(user.getId(), user.getName(), user.getUsername());
        }
}
    record DeleteUserResponse(String username, String status) {
        DeleteUserResponse(String username) {
            this( username, "Deleted successfully!");
        }
    }
}

