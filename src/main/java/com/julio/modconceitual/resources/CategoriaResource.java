/**
 * 
 */
package com.julio.modconceitual.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.julio.modconceitual.domain.Categoria;
import com.julio.modconceitual.services.CategoriaService;

/**
 * @author jsilvafr
 *
 */

@RestController
@RequestMapping(value = "/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaService service;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> find(@PathVariable Integer id) {

		Categoria categoria = service.buscar(id);
		return ResponseEntity.ok().body(categoria);

	}

}
