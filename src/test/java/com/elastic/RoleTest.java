package com.elastic;


import com.elastic.common.enums.StatusEnum;
import com.elastic.common.util.Utils;
import com.elastic.model.Narration;
import com.elastic.payload.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
public class RoleTest extends BaseTest {

    @Test
    public void createUpdateRoleTest() throws Throwable {
        CreateUpdateRoleRequest request = new CreateUpdateRoleRequest();
        request.setId(Utils.getMd5("JUNIT ROLE".trim().toLowerCase()));
        request.setRoleName("JUNIT ROLE");
        request.setStatus(StatusEnum.COMPLETED.name());
        request.setJobTitle("Managing Director");
        request.setExpectations(Collections.singletonList("NOTHING"));
        request.setHiddenPitfalls(Collections.singletonList("NOTHING"));
        request.setNarrations(Collections.singletonList(new Narration("Narrations", 0, null)));
        request.setImpactedWorks(Collections.singletonList("No Impact"));
        request.setPossibilities(Collections.singletonList("Great!"));
        request.setReportingRole("CEO");

        MvcResult result = mockMvc.perform(post("/api/role/create-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.asJsonString(request))).andReturn();

        CreateUpdateRoleResponse response = TestUtil.convert(result.getResponse().getContentAsString(), CreateUpdateRoleResponse.class);
        Assert.assertTrue(response.isStatus());
        log.info("==RESPONSE:{}", response.getId());
    }

    @Test
    public void getRoleTest() throws Throwable {
        createUpdateRoleTest();
        String id = Utils.getMd5("JUNIT ROLE".trim().toLowerCase());
        MvcResult result = mockMvc.perform(get("/api/role/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        GetRoleResponse response = TestUtil.convert(result.getResponse().getContentAsString(), GetRoleResponse.class);
        Assert.assertEquals(response.getId(), id);
        Assert.assertTrue(response.isStatus());
    }

    @Test
    public void getSmallRoleTest() throws Throwable {
        String id = Utils.getMd5("JUNIT ROLE".trim().toLowerCase());
        GetSmallRoleRequest request = new GetSmallRoleRequest();
        request.setId(id);

        MvcResult result = mockMvc.perform(post("/api/role/small")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.asJsonString(request))).andReturn();

        GetSmallRoleResponse response = TestUtil.convert(result.getResponse().getContentAsString(), GetSmallRoleResponse.class);
        Assert.assertEquals(id, response.getRoles().get(0).getId());
        Assert.assertTrue(response.isStatus());
    }

    @Test
    public void searchTest() throws Throwable{
        String id = Utils.getMd5("JUNIT ROLE".trim().toLowerCase());
        RoleSearchRequest request = new RoleSearchRequest();
        request.setId(id);

        MvcResult result = mockMvc.perform(post("/api/role/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.asJsonString(request))).andReturn();
        RoleSearchResponse response = TestUtil.convert(result.getResponse().getContentAsString(), RoleSearchResponse.class);
        log.info("RESPONSE:{}", objectMapper.writeValueAsString(response));
        Assert.assertEquals(id, response.getRoles().get(0).getId());
        Assert.assertTrue(response.isStatus());
    }

    @Test
    public void globalSearchTest() throws Throwable {
        String id = Utils.getMd5("JUNIT ROLE".trim().toLowerCase());
        GlobalSearchRequest request = new GlobalSearchRequest();
        request.setQuery(id);

        MvcResult result = mockMvc.perform(post("/api/role/global-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.asJsonString(request))).andReturn();

        RoleSearchResponse response = TestUtil.convert(result.getResponse().getContentAsString(), RoleSearchResponse.class);
        log.info("RESPONSE:{}", objectMapper.writeValueAsString(response));
        Assert.assertEquals(id, response.getRoles().get(0).getId());
        Assert.assertTrue(response.isStatus());
    }

}
