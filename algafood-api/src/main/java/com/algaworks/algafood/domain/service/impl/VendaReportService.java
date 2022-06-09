package com.algaworks.algafood.domain.service.impl;

import com.algaworks.algafood.domain.filter.VendaDiariaFilter;

import net.sf.jasperreports.engine.JRException;

public interface VendaReportService {

	byte[] emitirVendasDiarias(VendaDiariaFilter filtro, String timeOffset);
	
}
