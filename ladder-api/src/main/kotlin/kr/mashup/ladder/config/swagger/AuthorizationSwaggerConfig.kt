package kr.mashup.ladder.config.swagger

import kr.mashup.ladder.config.annotation.Auth
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.ParameterType
import springfox.documentation.service.RequestParameter
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.OperationBuilderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.swagger.common.SwaggerPluginSupport

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
class AuthorizationSwaggerConfig : OperationBuilderPlugin {

    override fun apply(context: OperationContext) {
        if (context.findAnnotation(Auth::class.java).isPresent) {
            context.operationBuilder()
                .requestParameters(listOf(authorizationHeader()))
                .authorizations(listOf(SecurityReference.builder()
                    .reference("Bearer")
                    .scopes(authorizationScopes())
                    .build()))
                .build()
        }
    }

    private fun authorizationHeader(): RequestParameter {
        return RequestParameterBuilder()
            .name(HttpHeaders.AUTHORIZATION)
            .required(false)
            .`in`(ParameterType.HEADER)
            .build()
    }

    private fun authorizationScopes(): Array<AuthorizationScope> {
        return arrayOf(AuthorizationScope("", ""))
    }

    override fun supports(delimiter: DocumentationType): Boolean {
        return true
    }

}
