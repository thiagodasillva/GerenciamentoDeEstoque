package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.UserRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.UserResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.User;
import com.thiagoRaimundo.controleEstoque.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;}

    public UserResponse getUser(Long idUser){
        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(()-> new ResourceNotFoundException("O usuario informado não existe. ID: "));
        return entityToDto(user);

    }

    public List<UserResponse> getUsers(){
        return userRepository.findByStatusTrue().stream().map(this::entityToDto).toList();
    }

    public UserResponse creatUser(UserRequest userRequest){
        User user = DTOToEntity(userRequest);
        userRepository.save(user);
        return entityToDto(user);
    }

    public void deleteLogico(Long idUser){
        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(()-> new ResourceNotFoundException("O usuario infomado não existe"));
        user.setStatus(false);
        userRepository.save(user);
    }


    public UserResponse updateUser(Long idUser, UserRequest userRequest){

        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(()-> new ResourceNotFoundException("O usuario infomado não existe"));

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setTipoUser(userRequest.getTipoUser());

        userRepository.save(user);
        return entityToDto(user);
    }


    private User DTOToEntity(UserRequest userRequest){
        return modelMapper.map(userRequest, User.class);
    }

    private UserResponse entityToDto(User user){
        return modelMapper.map(user, UserResponse.class);
    }
}
