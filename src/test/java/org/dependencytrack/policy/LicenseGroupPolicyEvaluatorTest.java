/*
 * This file is part of Dependency-Track.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (c) Steve Springett. All Rights Reserved.
 */
package org.dependencytrack.policy;

import org.dependencytrack.PersistenceCapableTest;
import org.dependencytrack.model.*;
import org.junit.Assert;
import org.junit.Test;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class LicenseGroupPolicyEvaluatorTest extends PersistenceCapableTest {

    @Test
    public void hasMatch() {
        License license = new License();
        license.setName("Apache 2.0");
        license.setLicenseId("Apache-2.0");
        license.setUuid(UUID.randomUUID());
        license = qm.persist(license);
        LicenseGroup lg = qm.createLicenseGroup("Test License Group");
        lg.setLicenses(Collections.singletonList(license));
        lg = qm.persist(lg);
        lg = qm.detach(LicenseGroup.class, lg.getId());
        license = qm.detach(License.class, license.getId());
        Policy policy = qm.createPolicy("Test Policy", Policy.Operator.ANY, Policy.ViolationState.INFO);
        PolicyCondition condition = qm.createPolicyCondition(policy, PolicyCondition.Subject.LICENSE_GROUP, PolicyCondition.Operator.IS, lg.getUuid().toString());
        policy = qm.detach(Policy.class, policy.getId());
        qm.detach(PolicyCondition.class, condition.getId());
        Component component = new Component();
        component.setResolvedLicense(license);
        PolicyEvaluator evaluator = new LicenseGroupPolicyEvaluator();
        Optional<PolicyConditionViolation> optional = evaluator.evaluate(policy, component);
        Assert.assertTrue(optional.isPresent());
    }

    @Test
    public void noMatch() {
        License license = new License();
        license.setName("Apache 2.0");
        license.setLicenseId("Apache-2.0");
        license.setUuid(UUID.randomUUID());
        license = qm.persist(license);
        LicenseGroup lg = qm.createLicenseGroup("Test License Group");
        lg = qm.persist(lg);
        lg = qm.detach(LicenseGroup.class, lg.getId());
        license = qm.detach(License.class, license.getId());
        Policy policy = qm.createPolicy("Test Policy", Policy.Operator.ANY, Policy.ViolationState.INFO);
        PolicyCondition condition = qm.createPolicyCondition(policy, PolicyCondition.Subject.LICENSE_GROUP, PolicyCondition.Operator.IS, lg.getUuid().toString());
        policy = qm.detach(Policy.class, policy.getId());
        qm.detach(PolicyCondition.class, condition.getId());
        Component component = new Component();
        component.setResolvedLicense(license);
        PolicyEvaluator evaluator = new LicenseGroupPolicyEvaluator();
        Optional<PolicyConditionViolation> optional = evaluator.evaluate(policy, component);
        Assert.assertFalse(optional.isPresent());
    }

    @Test
    public void wrongSubject() {
        License license = new License();
        license.setName("Apache 2.0");
        license.setLicenseId("Apache-2.0");
        license.setUuid(UUID.randomUUID());
        license = qm.persist(license);
        LicenseGroup lg = qm.createLicenseGroup("Test License Group");
        lg.setLicenses(Collections.singletonList(license));
        lg = qm.persist(lg);
        lg = qm.detach(LicenseGroup.class, lg.getId());
        license = qm.detach(License.class, license.getId());
        Policy policy = qm.createPolicy("Test Policy", Policy.Operator.ANY, Policy.ViolationState.INFO);
        qm.createPolicyCondition(policy, PolicyCondition.Subject.COORDINATES, PolicyCondition.Operator.IS, lg.getUuid().toString());
        Component component = new Component();
        component.setResolvedLicense(license);
        PolicyEvaluator evaluator = new LicenseGroupPolicyEvaluator();
        Optional<PolicyConditionViolation> optional = evaluator.evaluate(policy, component);
        Assert.assertFalse(optional.isPresent());
    }

    @Test
    public void wrongOperator() {
        License license = new License();
        license.setName("Apache 2.0");
        license.setLicenseId("Apache-2.0");
        license.setUuid(UUID.randomUUID());
        license = qm.persist(license);
        LicenseGroup lg = qm.createLicenseGroup("Test License Group");
        lg.setLicenses(Collections.singletonList(license));
        lg = qm.persist(lg);
        lg = qm.detach(LicenseGroup.class, lg.getId());
        license = qm.detach(License.class, license.getId());
        Policy policy = qm.createPolicy("Test Policy", Policy.Operator.ANY, Policy.ViolationState.INFO);
        qm.createPolicyCondition(policy, PolicyCondition.Subject.LICENSE_GROUP, PolicyCondition.Operator.MATCHES, lg.getUuid().toString());
        Component component = new Component();
        component.setResolvedLicense(license);
        PolicyEvaluator evaluator = new LicenseGroupPolicyEvaluator();
        Optional<PolicyConditionViolation> optional = evaluator.evaluate(policy, component);
        Assert.assertFalse(optional.isPresent());
    }

}
