package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.UserRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.UserResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.User;
import com.thiagoRaimundo.controleEstoque.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    public UserResponse getUser(Long idUser){
        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(()-> new ResourceNotFoundException("O usuario informado não existe. ID: "+ idUser));
        return entityToDto(user);

    }

    public List<UserResponse> getUsers(){
        return userRepository.findByStatusTrue().stream().map(this::entityToDto).toList();
    }

    @Transactional
    public UserResponse creatUser(UserRequest userRequest){

        if(userRepository.existsByEmailAndStatusTrue(userRequest.getEmail())){
            throw new RuntimeException("Email ja cadastrado: "+ userRequest.getEmail());
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword()); //adicionar função de codificação
        user.setTipoUser(userRequest.getTipoUser());
        user.setStatus(true);


        User savedUser = userRepository.save(user);
        return entityToDto(savedUser);
    }

    @Transactional
    public void deleteLogico(Long idUser){
        User user = userRepository.findByIdAndStatusTrue(idUser)
                .orElseThrow(()-> new ResourceNotFoundException("O usuario infomado não existe :"+ idUser));
        user.setStatus(false);
        userRepository.save(user);
    }


    @Transactional
    public UserResponse updateUser(Long idUser, UserRequest userRequest){

        User user = userRepository.findByIdAndStatusTrue(idUser)
                .orElseThrow(()-> new ResourceNotFoundException("O usuario infomado não existe: "+ idUser));

        if(!user.getEmail().equals(userRequest.getEmail()) && userRepository.existsByEmailAndStatusTrue(userRequest.getEmail())){
            throw new RuntimeException("O email informado ja foi cadastrado: "+ userRequest.getEmail());
        }

        //adicionar função de atualizar senha

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setTipoUser(userRequest.getTipoUser());

        User updatedUser = userRepository.save(user);
        return entityToDto(updatedUser);
    }

    //adicionar função de validação


    private User DTOToEntity(UserRequest userRequest){
        return modelMapper.map(userRequest, User.class);
    }

    private UserResponse entityToDto(User user){
        return modelMapper.map(user, UserResponse.class);
    }



}
