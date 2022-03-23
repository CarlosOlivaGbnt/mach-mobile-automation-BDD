package com.mach.tests.definitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Collections;
import java.util.Map;

public class CreateAccountDefinition {

    @When("cree una cuenta de usuario con los siguientes datos")
    public void cree_una_cuenta_de_usuario_con_los_siguientes_datos(io.cucumber.datatable.DataTable data) {

        Map<String, String> mapDatos = data.transpose().asMap(String.class, String.class);

/*        super.setUpData(mapDatos.get("userIdentifier"), "", Collections.singletonList(""),
                mapDatos.get("address"), "", "",
                "", "");*/

    }
    @Then("se mostrará el dashboard de la aplicación")
    public void se_mostrará_el_dashboard_de_la_aplicación() {


    }

}
