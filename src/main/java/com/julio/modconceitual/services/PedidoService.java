package com.julio.modconceitual.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.julio.modconceitual.domain.Pedido;
import com.julio.modconceitual.repositories.PedidoRepository;
import com.julio.modconceitual.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repository;

	public Pedido find(Integer id) {
		Optional<Pedido> obj = repository.findById(id);
		return obj.orElseThrow(() ->  new ObjectNotFoundException(
				"Objeto Nao encontrado! Id:" + id + ", Tipo: " + Pedido.class.getName()));
	}
}
