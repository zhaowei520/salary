package com.mzkj.controller.companyOriginal;

import com.fh.util.PageData;
import com.github.pagehelper.PageInfo;
import com.mzkj.facade.enums.HttpCode;
import com.mzkj.facade.vo.Result;
import com.mzkj.service.companyOriginal.CompanyInformationManager;
import com.mzkj.service.system.impl.UserService;
import com.mzkj.util.Const;
import com.mzkj.util.Jurisdiction;
import com.mzkj.util.UuidUtil;
import com.mzkj.vo.companyOriginal.CompanyInformationQueryVo;
import com.mzkj.vo.companyOriginal.CompanyInformationVo;
import com.mzkj.vo.companyOriginal.OriginalQueryVo;
import com.mzkj.vo.system.UserVo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 说明：原件管理客户信息
 * 创建人：CDCXH
 * 创建时间：2019-04-17
 */
@RestController
@RequestMapping(value = "/companyInformation")
@Api(tags = "CompanyInformationController", description = "原件管理客户信息接口")
public class CompanyInformationController {

    private static Logger logger = LogManager.getLogger(CompanyInformationController.class);
    String menuUrl = "/companyInformation"; //菜单地址(权限用)
    @Autowired
    private CompanyInformationManager companyinformationService;

    @Autowired
    private UserService userService;

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ApiOperation(value = "保存companyinformation", notes = "保存companyinformation")
    public Result<CompanyInformationVo> save(CompanyInformationVo companyinformationVo) {
        logger.info(Jurisdiction.getUsername() + "查询原件管理客户信息");
        Result<CompanyInformationVo> result = new Result<>();
        if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
            result.setMsg("没有操作权限，请联系管理员");
            result.setStatus(HttpCode.UNAUTHORIZED.getCode());
            return result;
        }
        companyinformationVo.setCompanyInformationId(UuidUtil.get32UUID());
        try {
            companyinformationVo = companyinformationService.save(companyinformationVo);
            result.setData(companyinformationVo);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            result.setStatus(HttpCode.ERROR.getCode());
            result.setSuccess(false);
            result.setMsg(e.toString());
        }
        return result;
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除template", notes = "删除template")
    public Result delete(@PathVariable("id") String companyInformationId) {
        logger.info(Jurisdiction.getUsername() + "删除原件管理客户信息");
        Result result = new Result<>();
        if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
            result.setMsg("没有操作权限，请联系管理员");
            result.setStatus(HttpCode.UNAUTHORIZED.getCode());
            return result;
        }
        try {
            companyinformationService.delete(companyInformationId);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            result.setStatus(HttpCode.ERROR.getCode());
            result.setSuccess(false);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "修改template", notes = "修改template")
    public Result edit(CompanyInformationVo companyinformationVo) {
        logger.info(Jurisdiction.getUsername() + "修改原件管理客户信息");
        Result result = new Result<>();
        if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
            result.setMsg("没有操作权限，请联系管理员");
            result.setStatus(HttpCode.UNAUTHORIZED.getCode());
            return result;
        }
        try {
            companyinformationService.edit(companyinformationVo);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            result.setStatus(HttpCode.ERROR.getCode());
            result.setSuccess(false);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询CompanyInformation", notes = "分页查询CompanyInformation")
    public Result<PageData> list(CompanyInformationQueryVo companyinformationQueryVo) {
        logger.info(Jurisdiction.getUsername() + "查看原件管理客户信息");
        Result<PageData> result = new Result<>();
        if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
            result.setMsg("没有操作权限，请联系管理员");
            result.setStatus(HttpCode.UNAUTHORIZED.getCode());
            return result;
        }
        try {
            PageInfo<CompanyInformationQueryVo> varList = companyinformationService.list(companyinformationQueryVo);
            //设置原件信息
            constOriginalListToString(varList);
            PageData resultdata = new PageData();
            resultdata.put("varList", varList);
            resultdata.put("QX", Jurisdiction.getHC());
            result.setData(resultdata);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            result.setStatus(HttpCode.ERROR.getCode());
            result.setSuccess(false);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 将originals转成string 原件名：人名，原件2，人名2
     * 设置出库信息
     * return
     * Author luosc
     * param
     * Date 2019-04-22 9:02
     */
    private void constOriginalListToString(PageInfo<CompanyInformationQueryVo> varList) throws Exception {
        if (varList != null && varList.getList() != null && varList.getList().size() > 0) {
            for (CompanyInformationQueryVo companyInformationQueryVo : varList.getList()) {
                List<OriginalQueryVo> originalQueryVos = companyInformationQueryVo.getOriginalList();
                if (originalQueryVos != null && originalQueryVos.size() > 0) {
                    String result = "";
                    String originalOutStatusInformation = "";
                    for (OriginalQueryVo original : originalQueryVos) {
                        String originalName = original.getOriginalName();//原件名
                        String originalHolder = original.getOriginalHolder();//原件持有人
                        if (!StringUtils.isEmpty(original.getOriginalOutStatus()) && original.getOriginalOutStatus().equals(Const.ORIGINAL_OUT_STATUS_2) && !StringUtils.isEmpty(originalHolder)) {
                            //将当前持有人userName 转name
                            UserVo userVo = userService.findByUsername(originalHolder);
                            if (userVo != null && !StringUtils.isEmpty(userVo.getName())) {
                                result += originalName + ":" + userVo.getName() + ",";
                            }
                        } else if (!StringUtils.isEmpty(original.getOriginalHoldStatus()) && original.getOriginalHoldStatus().equals(Const.ORIGINAL_HOLD_STATUS_0)) {
                            //无原件
                            result += originalName + ":无,";
                        } else if (!StringUtils.isEmpty(original.getOriginalHoldStatus()) && original.getOriginalHoldStatus().equals(Const.ORIGINAL_HOLD_STATUS_1)) {
                            //在客户处
                            result += originalName + ":客户处,";
                        }

                        //流转状态为 出库中
                        if (!StringUtils.isEmpty(original.getOriginalOutStatus()) && original.getOriginalOutStatus().equals(Const.ORIGINAL_OUT_STATUS_1)) {
                            //如果原件持有人和当前登录人相同
                            if (!StringUtils.isEmpty(original.getOriginalHolder()) && original.getOriginalHolder().equals(Jurisdiction.getUsername())) {
                                originalOutStatusInformation += original.getOriginalName() + ":出库中,";
                            }
                            //出库对象和当前登录人相同
                            else if (!StringUtils.isEmpty(original.getOriginalOutTo()) && original.getOriginalOutTo().equals(Jurisdiction.getUsername())) {
                                originalOutStatusInformation += original.getOriginalName() + ":借入待确认,";
                            }
                        }
                        //待借入
                        if (!StringUtils.isEmpty(original.getOriginalOutStatus()) && original.getOriginalOutStatus().equals(Const.ORIGINAL_OUT_STATUS_3)) {
                            //如果原件持有人和当前登录人相同
                            if (!StringUtils.isEmpty(original.getOriginalHolder()) && original.getOriginalHolder().equals(Jurisdiction.getUsername())) {
                                originalOutStatusInformation += original.getOriginalName() + ":借出待确认,";
                            }
                            //出库对象和当前登录人相同
                            else if (!StringUtils.isEmpty(original.getOriginalOutTo()) && original.getOriginalOutTo().equals(Jurisdiction.getUsername())) {
                                originalOutStatusInformation += original.getOriginalName() + ":待借入,";
                            }
                        }

                    }
                    companyInformationQueryVo.setOriginalListString(result);
                    companyInformationQueryVo.setOriginalInformation(originalOutStatusInformation);
                }
            }
        }
    }

    /**
     * 根据ID查询
     * return
     * Author luosc
     * param
     * Date 2019-04-23 8:46
     */
    @RequestMapping(value = "/findById/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID查询CompanyInformation", notes = "根据ID查询CompanyInformation")
    public Result findCompanyInformationById(@PathVariable("id") String companyInformationId) {
        logger.info(Jurisdiction.getUsername() + "findById查看原件管理客户信息");
        Result result = new Result();
        try {
            CompanyInformationVo companyInformationVo = companyinformationService.findById(companyInformationId);
            result.setData(companyInformationVo);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            result.setStatus(HttpCode.ERROR.getCode());
            result.setSuccess(false);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 统计当前登录人持有原件数量、需确认条数、出库中数量、待借入数量
     * return
     * Author luosc
     * param
     * Date 2019-04-30 17:15
     */
    @RequestMapping(value = "/getCount", method = RequestMethod.GET)
    @ApiOperation(value = "统计", notes = "统计")
    public Result<Map<String, Integer>> holdCountAndToBeConfirmedCountAndOutgoingCountAndLoanInCount() {
        Result<Map<String, Integer>> result = new Result<>();

        try {
            Map<String, Integer> countMap = companyinformationService.holdCountAndToBeConfirmedCountAndOutgoingCountAndLoanInCount();
            result.setData(countMap);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            result.setStatus(HttpCode.ERROR.getCode());
            result.setSuccess(false);
            result.setMsg(e.getMessage());
        }
        return result;
    }

}
