package com.julio.modconceitual.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.julio.modconceitual.domain.Cliente;
import com.julio.modconceitual.repositories.ClienteRepository;
import com.julio.modconceitual.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;

	public Cliente find(Integer id) {
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() ->  new ObjectNotFoundException(
				"Objeto Nao encontrado! Id:" + id + ", Tipo: " + Cliente.class.getName()));
	}
}
