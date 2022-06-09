package com.algaworks.algafood.core.jackson;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
/*
 * Esta classe é utilizada para realizar a serialização de objetos onde na sua saida o Jackson faria a serialização do mesmo.
 * Neste caso estamos serializando o objeto Page, mas poderia ser qualquer outro objeto um Restaurante ou Pedido
 * */

@JsonComponent
public class PageJsonSerializer extends JsonSerializer<Page<?>> {

	@Override
	public void serialize(Page<?> page, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();  //Iniciando a criação do novo objeto
		
		gen.writeObjectField("content", page.getContent());  //Criando um novo objeto com o nome content
		gen.writeNumberField("size", page.getSize());
		gen.writeNumberField("totalElements", page.getTotalElements());
		gen.writeNumberField("totalPages", page.getTotalPages());
		gen.writeNumberField("number", page.getNumber());
		
		gen.writeEndObject();
		
	}
	
}

/*ANTES DA SERIALIZAÇÃO*/
/*{
    "content": [
        {
            "id": 3,
            "nome": "Argentina"
        },
        {
            "id": 4,
            "nome": "Brasileira"
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 2,
        "pageSize": 1,
        "pageNumber": 2,
        "unpaged": false,
        "paged": true
    },
    "last": false,
    "totalPages": 5,
    "totalElements": 5,
    "number": 2,
    "size": 1,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 1,
    "first": false,
    "empty": false
}
 * É POSSIVEL VER QUE O OBJETO PAGE É BEM GRANDE E COM DIVERSOS CAMPOS NÃO UTILIZADOS*/
/************************************************************/
/*DEPOIS DA SERIALIZAÇÃO*/
/*
 * {
    "contente": [
        {
            "id": 3,
            "nome": "Argentina"
        },
        {
            "id": 4,
            "nome": "Brasileira"
        }
    ],
    "size": 2,
    "totalElements": 4,
    "totalPages": 2,
    "number": 0
}
 * */
