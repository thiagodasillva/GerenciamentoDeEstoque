package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.LoteRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.LoteResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.Lote;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.repository.LoteRepository;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoteService {
    private LoteRepository loteRepository;
    private ProductRepository productRepository;
    private ModelMapper modelMapper;
    //private static final Logger log = (Logger) LoggerFactory.getLogger(LoteService.class);


    public LoteService(LoteRepository loteRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.loteRepository = loteRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public LoteResponse creatLote(LoteRequest loteRequest){

        Product product = productRepository.findByIdAndStatusTrue(loteRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. ID: "+ loteRequest.getProductId()));

        if(loteRepository.existsByCodigo(loteRequest.getCodigo())){
            throw new IllegalArgumentException("ja existe um lote com o código: "+loteRequest.getCodigo());
        }

        Lote lote = new Lote();
        lote.setProduct(product);
        lote.setQuantProdutos(loteRequest.getQuantProdutos());
        lote.setCodigo(loteRequest.getCodigo());
        lote.setValidate(loteRequest.getValidate());
        lote.setStatus(true);


        Lote saverLote = loteRepository.save(lote);
        return entityToDTO(saverLote);

    }

    public List<LoteResponse> getLotes(){

        return loteRepository.findByStatusTrue()
                .stream()
                .map(this:: entityToDTO)
                .collect(Collectors.toList());
    }


    public LoteResponse getLoteById(Long idLote){
        Lote lote = loteRepository.findByIdAndStatusTrue(idLote)
                .orElseThrow(() -> new ResourceNotFoundException("Não Foi encontrado nenhum lote com o ID: "+ idLote));
        return entityToDTO(lote);
    }

    public LoteResponse getLoteByCodigo(String codigoLote){
        Lote lote = loteRepository.findByCodigoAndStatusTrue(codigoLote)
                .orElseThrow(() -> new ResourceNotFoundException("Não Foi encontrado nenhum lote com o Codigo: "+ codigoLote));
        return entityToDTO(lote);
    }


    public List<LoteResponse> getLotesByProdutosOrderByValidadeDate(Long idProduct){
        if(!productRepository.existsById(idProduct)){
            throw new ResourceNotFoundException("O produto não encontrado. ID: "+ idProduct );
        }
        return loteRepository.findByProductIdAndStatusTrueOrderByValidateAsc(idProduct)
                .stream()
                .map(this::entityToDTO)
                .toList();

    }

    @Transactional
    public void deleteLogicoDeLote (Long idLote){
        Lote lote = loteRepository.findByIdAndStatusTrue(idLote)
                .orElseThrow(()-> new ResourceNotFoundException("Lote não encontrado. ID: "+ idLote));
        lote.setStatus(false);
        loteRepository.save(lote);
    }

    @Transactional
    public LoteResponse updateLote(Long idLote, LoteRequest loteRequest){
        Lote lote = loteRepository.findByIdAndStatusTrue(idLote)
                .orElseThrow(()-> new ResourceNotFoundException("Lote não encontrado. ID: "+ idLote));


        if(lote.getProduct().getId().equals(loteRequest.getProductId())){

            Product product = productRepository.findByIdAndStatusTrue(loteRequest.getProductId())
                    .orElseThrow(()-> new ResourceNotFoundException("O produto informado no lote não existe. Id Produto: "+ loteRequest.getProductId()));

            lote.setProduct(product);
        }


        lote.setQuantProdutos(loteRequest.getQuantProdutos());
        lote.setValidate(loteRequest.getValidate());
        lote.setCodigo(loteRequest.getCodigo());

        Lote updatedLote= loteRepository.save(lote);

        return entityToDTO(updatedLote);
    }


    private Lote dtoToEntity(LoteRequest request){
        return modelMapper.map(request, Lote.class);
    }

    private LoteResponse entityToDTO(Lote lote){
        return modelMapper.map(lote, LoteResponse.class);

    }


}
