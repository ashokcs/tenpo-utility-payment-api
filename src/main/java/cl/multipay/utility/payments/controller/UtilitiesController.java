package cl.multipay.utility.payments.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.Utility;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.service.MulticajaService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilitiesController
{
	private final MulticajaService multicajaService;

	public UtilitiesController(final MulticajaService multicajaService)
	{
		this.multicajaService = multicajaService;
	}

	@GetMapping("/v1/utilities")
	public ResponseEntity<List<Utility>> get()
	{
		return ResponseEntity.ok(multicajaService.getUtilities().orElseThrow(ServerErrorException::new));
	}
}
