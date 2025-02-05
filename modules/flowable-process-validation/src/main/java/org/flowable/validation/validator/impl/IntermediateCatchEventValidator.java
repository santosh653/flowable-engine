/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.validation.validator.impl;

import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ConditionalEventDefinition;
import org.flowable.bpmn.model.EventDefinition;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.IntermediateCatchEvent;
import org.flowable.bpmn.model.MessageEventDefinition;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SignalEventDefinition;
import org.flowable.bpmn.model.TimerEventDefinition;
import org.flowable.bpmn.model.VariableListenerEventDefinition;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.Problems;
import org.flowable.validation.validator.ProcessLevelValidator;

/**
 * @author jbarrez
 */
public class IntermediateCatchEventValidator extends ProcessLevelValidator {

    @Override
    protected void executeValidation(BpmnModel bpmnModel, Process process, List<ValidationError> errors) {
        List<IntermediateCatchEvent> intermediateCatchEvents = process.findFlowElementsOfType(IntermediateCatchEvent.class);
        for (IntermediateCatchEvent intermediateCatchEvent : intermediateCatchEvents) {
            EventDefinition eventDefinition = null;
            if (!intermediateCatchEvent.getEventDefinitions().isEmpty()) {
                eventDefinition = intermediateCatchEvent.getEventDefinitions().get(0);
            }

            if (eventDefinition == null) {

                Map<String, List<ExtensionElement>> extensionElements = intermediateCatchEvent.getExtensionElements();
                if (!extensionElements.isEmpty()) {
                    List<ExtensionElement> eventTypeExtensionElements = intermediateCatchEvent.getExtensionElements().get("eventType");
                    if (eventTypeExtensionElements != null && !eventTypeExtensionElements.isEmpty()) {
                        return;
                    }
                }

                addError(errors, Problems.INTERMEDIATE_CATCH_EVENT_NO_EVENTDEFINITION, process, intermediateCatchEvent, "No event definition for intermediate catch event ");
                
            } else {
                if (!(eventDefinition instanceof TimerEventDefinition) && 
                        !(eventDefinition instanceof SignalEventDefinition) && 
                        !(eventDefinition instanceof MessageEventDefinition) && 
                        !(eventDefinition instanceof ConditionalEventDefinition) && 
                        !(eventDefinition instanceof VariableListenerEventDefinition)) {
                    
                    addError(errors, Problems.INTERMEDIATE_CATCH_EVENT_INVALID_EVENTDEFINITION, process, intermediateCatchEvent, "Unsupported intermediate catch event type");
                }
            }
        }
    }

}
