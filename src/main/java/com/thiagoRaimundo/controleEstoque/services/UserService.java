package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.models.User;
import com.thiagoRaimundo.controleEstoque.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id){
        Optional<User> userOpr = userRepository.findById(id);
        if(userOpr.isPresent()){
            User user = userOpr.get();
            if(user.getStatus() == true){
                return user;
            }
            return null;
        }
        return null;
    }

    public Collection<User> getUsers(){
        return userRepository.findAll();
    }

    public User creatUser(User u){
        userRepository.save(u);
        return u;
    }

    public void deleteUser(Long id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setStatus(false);
        }
    }


    public User updateUser(User u, Long id){
        Optional<User> userOptional= userRepository.findById(id);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setName(u.getName());
            user.setEmail(u.getEmail());
            user.setTipoUser(user.getTipoUser());

            return user;
        }
        return null;
    }
}
