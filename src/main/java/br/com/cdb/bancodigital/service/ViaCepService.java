package br.com.cdb.bancodigital.service;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.cdb.bancodigital.entity.Endereco;

@Service
public class ViaCepService {

	public Endereco getEndereco(String cep) throws IOException {
		Endereco end = null;
		HttpGet request = new HttpGet("https://viacep.com.br/ws/" + cep + "/json/");

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
				CloseableHttpResponse response = httpClient.execute(request)) {

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 400) {
				throw new IllegalArgumentException("Cep inválido");
			}

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String result = EntityUtils.toString(entity);
				Gson gson = new Gson();
				end = gson.fromJson(result, Endereco.class);
			}

		}

		return end;
	}

}
