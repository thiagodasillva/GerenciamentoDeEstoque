package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.LoteRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.LoteResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.Lote;
import com.thiagoRaimundo.controleEstoque.exceptions.LoteNotFoundException;
import com.thiagoRaimundo.controleEstoque.repository.LoteRepository;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class LoteService {

    private LoteRepository loteRepository;
    private ProductRepository productRepository;
    private ModelMapper modelMapper;
    private static final Logger log = (Logger) LoggerFactory.getLogger(LoteService.class);


    public LoteService(LoteRepository loteRepository, ProductRepository productRepository) {
        this.loteRepository = loteRepository;
        this.productRepository = productRepository;

    }

    // CRUD, validar datas

    public LoteResponse creatLote(LoteRequest loteRequest){

        if(loteRequest.getValidate().isBefore(LocalDate.now())){
            throw new RuntimeException("Data de validade não pode ser no passado"); // mudar para um exception tratado
        }
        if(!productRepository.existsById(loteRequest.getProduct().getId())){
            throw new ResourceNotFoundException("Produto não encontrado. ID: "+ loteRequest.getProduct().getId());
        }

        Lote lote = dtoToEntity(loteRequest);
        loteRepository.save(lote);

        return entityToDTO(lote);

    }

    public List<LoteResponse> getLotes(){

        return loteRepository.findByStatusTrue().stream().map(this:: entityToDTO).collect(Collectors.toList());
    }


    public LoteResponse getLote(Long idLote){
        Lote lote = loteRepository.findByIdAndStatusTrue(idLote).orElseThrow(() -> new LoteNotFoundException("Não Foi encontrado nenhum lote com o ID: "+ idLote));
        return entityToDTO(lote);
    }


    public List<LoteResponse> buscarLotesDeProdutosOrdenadosPorDataDeValidade(Long id){
        if(!productRepository.existsById(id)){
            throw new ResourceNotFoundException("O produto não encontrado. ID: "+ id );
        }

        return loteRepository.findByProductIdAndStatusTrueOrderByDataValidadeAsc(id).stream().map(this::entityToDTO).toList();

    }

    public void deleteLogicoDeLote (Long idLote){
        Lote lote = loteRepository.findByIdAndStatusTrue(idLote).orElseThrow(()-> new ResourceNotFoundException("Lote não encontrado. ID: "+ idLote));
        lote.setStatus(false);
        loteRepository.save(lote);
    }

    public LoteResponse updateLote(Long idLote, LoteRequest loteRequest){
        Lote lote = loteRepository.findByIdAndStatusTrue(idLote).orElseThrow(()-> new ResourceNotFoundException("Lote não encontrado. ID: "+ idLote));

        if(!productRepository.existsById(lote.getProduct().getId())){
            throw new ResourceNotFoundException("O produto informado no lote não existe. Id Produto: "+ loteRequest.getProduct().getId());
        }

        lote.setQuantAtual(loteRequest.getQuantAtual());
        lote.setProduct(loteRequest.getProduct());
        lote.setValidate(loteRequest.getValidate());
        loteRepository.save(lote);

        return entityToDTO(lote);
    }


    private Lote dtoToEntity(LoteRequest request){
        return modelMapper.map(request, Lote.class);
    }

    private LoteResponse entityToDTO(Lote lote){
        return modelMapper.map(lote, LoteResponse.class);

    }







}
