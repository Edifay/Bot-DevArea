package devarea.judge.core.io;

import devarea.judge.JudgeException;
import devarea.judge.core.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

public class Client {

    private final URI submissionUrl;
    private final String parameter;

    public Client(String url, boolean isEncode) {
        submissionUrl = URI.create(url).resolve("/submissions/");
        parameter = "?base64_encoded=" + isEncode;
    }

    public String createSubmission(Map<String, String> entity) throws JudgeException {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Token> result = restTemplate.postForEntity(submissionUrl.resolve(parameter), entity, Token.class);
            if (result.getBody() == null || result.getBody().getToken() == null) {
                throw new JudgeException("Erreur interne du serveur.");
            }
            return result.getBody().getToken();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new JudgeException(e.getRawStatusCode() + " " + e.getStatusText());
        } catch (RestClientException e) {
            throw new JudgeException(e.getCause().getMessage());
        }
    }

    public ResponseBuilder getSubmission(String token) throws JudgeException {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<ResponseBuilder> result = restTemplate.getForEntity(submissionUrl.resolve(token + parameter), ResponseBuilder.class);
            return result.getBody();
        } catch (HttpClientErrorException e) {
            throw new JudgeException(e.getRawStatusCode() + " " + e.getStatusText());
        }
    }
}
