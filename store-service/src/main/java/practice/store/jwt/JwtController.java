package practice.store.jwt;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import practice.store.jwt.payload.JwtLoginRequestPayload;
import practice.store.jwt.payload.JwtLoginResponsePayload;
import practice.store.jwt.payload.JwtLogoutResponsePayload;

@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Api(tags = "JWT")
@RestController
@CrossOrigin
public class JwtController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @ApiOperation(value = "This method is used to login.")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity loginAndGenerateAuthenticationToken(@RequestBody JwtLoginRequestPayload authenticationRequest) throws Exception {
        checkIfCredentialsAreCorrect(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        final UserDetails userDetails = jwtService.loadUserByUsername(authenticationRequest.getEmail());
        final JwtLoginResponsePayload jwtLoginResponsePayload = jwtService.buildResponseLogin(userDetails);

        return new ResponseEntity<>(jwtLoginResponsePayload, HttpStatus.CREATED);
    }

    @ApiOperation(value = "This method is used to logout.")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout() {
        jwtService.addTokenToBlackListRepository();
        JwtLogoutResponsePayload jwtLogoutResponsePayload = jwtService.buildResponseLogout();

        return new ResponseEntity<>(jwtLogoutResponsePayload, HttpStatus.OK);
    }

    private void checkIfCredentialsAreCorrect(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
