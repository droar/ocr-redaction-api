package com.droar.ocr.redaction.api.web.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import com.droar.ocr.redaction.api.common.model.PageInfoDTO;
import com.droar.ocr.redaction.api.common.model.PostDocTextReplaceResponseDTO;
import com.droar.ocr.redaction.api.common.model.PostOcrExtractResponseDTO;
import com.droar.ocr.redaction.api.common.model.TextInfoDTO;
import com.droar.ocr.redaction.api.common.util.Utils;
import com.droar.ocr.redaction.api.common.util.tesseract.TextInfo;

/**
 * 
 * @author droar
 *
 */
@Component
public class FileMapper {

  /**
   * Transforms OCR Results to DTO
   * 
   * @param mpOcrResults
   * @return
   */
  public PostOcrExtractResponseDTO transformToDTO(Map<Integer, List<TextInfo>> mpOcrResults) {
    PostOcrExtractResponseDTO processedTextDTO = new PostOcrExtractResponseDTO();

    if (MapUtils.isNotEmpty(mpOcrResults)) {
      List<PageInfoDTO> lstPagesInfo = new ArrayList<>();

      for (Entry<Integer, List<TextInfo>> res : mpOcrResults.entrySet()) {
        String pageNum = String.valueOf(res.getKey());
        List<TextInfo> lstTextInfo = res.getValue();

        List<TextInfoDTO> lstTextInfoDTO = Optional.ofNullable(lstTextInfo).orElse(new ArrayList<>()).stream().map(i -> {
          // Mapping
          TextInfoDTO dto = new TextInfoDTO();
          dto.setWordText(i.getText());
          // We transform the positions to pixel, we dont want them on points.
          dto.setPosX(String.valueOf(Utils.toPixel(i.getBboxRect().getX())));
          dto.setPosY(String.valueOf(Utils.toPixel(i.getBboxRect().getY())));
          dto.setPosXR(String.valueOf(Utils.toPixel(i.getBboxRect().getRight())));
          dto.setPosYR(String.valueOf(Utils.toPixel(i.getBboxRect().getTop())));
          return dto;

        }).collect(Collectors.toList());

        PageInfoDTO pageInfoDTO = new PageInfoDTO();
        pageInfoDTO.setPageNumber(pageNum);
        pageInfoDTO.setTextInformation(lstTextInfoDTO);

        // We add the page
        lstPagesInfo.add(pageInfoDTO);
      }

      // We add the final info
      processedTextDTO.setLstPagesInfo(lstPagesInfo);
    }

    return processedTextDTO;
  }


  /**
   * Transforms multiPartFile to DTO
   * 
   * @param file
   * @return
   */
  public PostDocTextReplaceResponseDTO transformToDTO(byte[] docBytes) {
    PostDocTextReplaceResponseDTO fileDTO = new PostDocTextReplaceResponseDTO();
    if (docBytes != null) fileDTO.setFileBytes(docBytes);

    return fileDTO;
  }

}
