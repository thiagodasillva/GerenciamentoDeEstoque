package com.thiagoRaimundo.controleEstoque.buscaVoz.transcription;

import com.thiagoRaimundo.controleEstoque.exceptions.TranscricaoAudioExceptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TranscriptionClient {

    private WebClient webClient;

    public TranscriptionClient(@Value("${transcription.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public String transcrever(byte[] audioBytes, String filename) {

        try{
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", audioBytes)
                .filename(filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM);

        return webClient.post()
                .uri("/transcrever")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .bodyToMono(TranscriptionResponse.class)
                .map(TranscriptionResponse::getTexto)
                .block(); // block é aceitável se não for reativo end-to-
    }
        catch (Exception e){
            throw new TranscricaoAudioExceptionException("Não foi possivel decodificar o audio, tente falar mais claro ou difite a busca.", e);
        }


    }

    // Classe interna para mapear a resposta do Python
    private static class TranscriptionResponse {
        private String texto;
        public String getTexto() { return texto; }
        public void setTexto(String texto) { this.texto = texto; }
    }




    }
