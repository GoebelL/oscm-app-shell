/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2018                                           
 *                                                                                                                                 
 *  Creation Date: Aug 2, 2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.shell.business.actions;

import static org.oscm.app.shell.business.ConfigurationKey.SCRIPT_FILE;
import static org.oscm.app.shell.business.ConfigurationKey.SM_ERROR_MESSAGE;
import static org.oscm.app.shell.business.actions.StatemachineEvents.FAILED;

import org.oscm.app.shell.business.Configuration;
import org.oscm.app.shell.business.Script;
import org.oscm.app.v2_0.data.InstanceStatus;
import org.oscm.app.v2_0.data.ProvisioningSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oscm.app.statemachine.api.StateMachineAction;

public class ProvisioningActions {

    private static final Logger LOG = LoggerFactory.getLogger(ProvisioningActions.class);

    Actions getActions() {
	return new Actions();
    }

    @StateMachineAction
    public String executeScript(String instanceId, ProvisioningSettings settings, InstanceStatus result) {
	Configuration config = new Configuration(settings);
	try {
	    Script script = new Script(config.getSetting(SCRIPT_FILE));
	    script.insertServiceParameter(settings);
	    return getActions().executeScript(instanceId, settings, result, script);
	} catch (Exception e) {
	    LOG.error("Couldn't execute shell script", e);
	    config.setSetting(SM_ERROR_MESSAGE, e.getMessage());
	    return FAILED;
	}
    }

    @StateMachineAction
    public String consumeScriptOutput(String instanceId, ProvisioningSettings settings, InstanceStatus result)
	    throws Exception {

	return getActions().consumeScriptOutput(instanceId, settings, result);
    }

    @StateMachineAction
    public String finish(String instanceId, ProvisioningSettings settings, InstanceStatus result) {
	result.setIsReady(true);
	return StatemachineEvents.SUCCESS;
    }

    @StateMachineAction
    public String finalizeProvisioning(@SuppressWarnings("unused") String instanceId, ProvisioningSettings settings,
	    InstanceStatus result) {

	LOG.debug("Successfully finished.");
	result.setIsReady(true);
	return StatemachineEvents.SUCCESS;
    }

}
