package agroFiscal.service;

import agroFiscal.model.Fornecedor;
import agroFiscal.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    public Fornecedor salvar(Fornecedor fornecedor) {
        
        Optional<Fornecedor> existente = fornecedorRepository.findByCnpj(fornecedor.getCnpj());
        
        if(existente.isPresent() && !existente.get().getId().equals(fornecedor.getId())) {
            throw new IllegalArgumentException("Já existe um fornecedor cadastrado com este CNPJ.");
        }
        
        return fornecedorRepository.save(fornecedor);
    }
}