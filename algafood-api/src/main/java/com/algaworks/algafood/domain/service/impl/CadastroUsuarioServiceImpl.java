package com.algaworks.algafood.domain.service.impl;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exeption.NegocioException;
import com.algaworks.algafood.domain.exeption.UsuarioNaoEncontradoException;
import com.algaworks.algafood.domain.model.Grupo;
import com.algaworks.algafood.domain.model.Usuario;
import com.algaworks.algafood.domain.repository.UsuarioRepository;

@Service
public class CadastroUsuarioServiceImpl {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CadastroGrupoServiceImpl cadastroGrupo;
    
    @Transactional
    public Usuario salvar(Usuario usuario) {
    	
    	usuarioRepository.detach(usuario); // A função detach é implementada no CustomJpaRepository e é utilizada para nos casos onde for realizada alguma alteração na entidade usuário
    	 // a mesma não ser executada de imediato pelo JPA, pois este comportamento geraria um bug, onde seria possivel o usuário alteraro seu email sem antes validar se o email informado
         // é unico ou não.
    	// O comportamento padrão do JPA é que assim que a entidade sofre uma alteração antes de uma nova transação ser realizada ele deve descarregar todas as alterações realizadas em uma
    	// entidade por isso que esta função é utilizada para se desconectar do banco e apenas depois da validação realizada a atualização do mesmo no banco.
    	
    	Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
    	
    	if(usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)){
    		throw new NegocioException(String.format("Já existe um usuário cadastrado com o e-mail: %s", usuario.getEmail()));
    	}
    	
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void alterarSenha(Long usuarioId, String senhaAtual, String novaSenha) {
        Usuario usuario = buscarOuFalhar(usuarioId);
        
        if (usuario.senhaNaoCoincideCom(senhaAtual)) {
            throw new NegocioException("Senha atual informada não coincide com a senha do usuário.");
        }
        
        usuario.setSenha(novaSenha);
    }
    
    @Transactional
    public void desassociarGrupo(Long usuarioId, Long grupoId) {
        Usuario usuario = buscarOuFalhar(usuarioId);
        Grupo grupo = cadastroGrupo.buscarOuFalhar(grupoId);
        
        usuario.removerGrupo(grupo);
    }

    @Transactional
    public void associarGrupo(Long usuarioId, Long grupoId) {
        Usuario usuario = buscarOuFalhar(usuarioId);
        Grupo grupo = cadastroGrupo.buscarOuFalhar(grupoId);
        
        usuario.adicionarGrupo(grupo);
    }

    public Usuario buscarOuFalhar(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));
    }            
}
