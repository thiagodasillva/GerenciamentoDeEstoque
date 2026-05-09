package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.Lote;
import com.thiagoRaimundo.controleEstoque.exceptions.LoteNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.repository.LoteRepository;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class LoteService {

    private LoteRepository loteRepository;
    private ProductRepository productRepository;

    public LoteService(LoteRepository loteRepository, ProductRepository productRepository) {
        this.loteRepository = loteRepository;
        this.productRepository = productRepository;

    }

    // CRUD, validar datas

    public Lote creatLote(Lote l){

        if(l.getValidate().isBefore(LocalDate.now())){
            throw new RuntimeException("Data de validade não pode ser no passado");
        }
        Product product = productRepository.findById(l.getProduct().getId()).orElseThrow(()-> new ResourceNotFoundException("Produto não encontrado"));


        return loteRepository.save(l);

    }

    public Collection<Lote> getLotes(){
        return loteRepository.findAll();
    }
    public Lote getLote(Long idLote){
        Optional<Lote> l = loteRepository.findById(idLote);
        if(l.isPresent()){
            return l.get();
        }
        throw new LoteNotFoundException("Não foi possivel encontrar os lotes");

    }

    public List<Lote> buscarProductOrderByValidade(Long id){
        Product p = productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Produto não consta no cadastro"));
        return loteRepository.findByProductIdOrderByDataValidadeAsc(p.getId());

    }

    public void deleteLote (Long idLote){
        Optional<Lote> lOpt = loteRepository.findById(idLote);
        if (lOpt.isPresent()){
            Lote lote = lOpt.get();
            lote.setStatus(false);
            loteRepository.save(lote);
        }

    }

    public Lote updateLote(Lote l, Long id){
        Optional<Lote> lOpt = loteRepository.findById(id);
        if(lOpt.isPresent()){
            Lote lote = lOpt.get();
            lote.setQuantAtual(l.getQuantAtual());
            lote.setProduct(l.getProduct());
            lote.setValidate(l.getValidate());
            loteRepository.save(lote);
            return lote;

        }

        throw new RuntimeException("O id informado não corresponde a nenhum lote existente ");
    }







}
