package com.julio.modconceitual.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.julio.modconceitual.domain.Cidade;
import com.julio.modconceitual.domain.Cliente;
import com.julio.modconceitual.domain.Endereco;
import com.julio.modconceitual.domain.enums.Perfil;
import com.julio.modconceitual.domain.enums.TipoCliente;
import com.julio.modconceitual.dto.ClienteDTO;
import com.julio.modconceitual.dto.ClienteNewDTO;
import com.julio.modconceitual.repositories.ClienteRepository;
import com.julio.modconceitual.repositories.EnderecoRepository;
import com.julio.modconceitual.security.UserSS;
import com.julio.modconceitual.services.exceptions.AuthorizationException;
import com.julio.modconceitual.services.exceptions.DataIntegrityException;
import com.julio.modconceitual.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private ImageService imageService;

	@Value("${img.prefix.client.profile}")
	private String prefix;

	public Cliente find(Integer id) {
		
		UserSS user = UserService.authenticated();
		if (user==null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() ->  new ObjectNotFoundException(
				"Objeto Nao encontrado! Id:" + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	public Cliente update(Cliente obj) {
		Cliente newOBJ = find(obj.getId());
		updateData(newOBJ, obj);
		return repository.save(newOBJ);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possivel excluir o cliente porque há pedidos relacionados!");
		}
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}

	public List<Cliente> findAll() {
		return repository.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objDTO) {
		Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(), TipoCliente.toEnum(objDTO.getTipoCliente()), pe.encode(objDTO.getSenha()));
		Cidade cid = new Cidade(objDTO.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), objDTO.getComplemento(), objDTO.getBairro(), objDTO.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDTO.getTelefone1());
		if(objDTO.getTelefone2() != null) {
			cli.getTelefones().add(objDTO.getTelefone2());
		}
		if(objDTO.getTelefone2() != null) {
			cli.getTelefones().add(objDTO.getTelefone3());
		}
		return cli;
	}
	
	private void updateData(Cliente newOBJ, Cliente obj) {
		newOBJ.setNome(obj.getNome());
		newOBJ.setEmail(obj.getEmail());
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}

		BufferedImage jpgImage = imageService.getJpgImageFromFile(multipartFile);
		String fileName = prefix + user.getId() + ".jpg";

		return s3Service.uploadFile(imageService.getInputStream(jpgImage, "jpg"), fileName, "image");
	}
}
