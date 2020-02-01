package com.github.lyrric.common.vo;

import com.github.lyrric.common.entity.TorrentInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @Builder
public class TorrentPageVO implements Serializable {

	private List<TorrentInfo> list;
	private Long total;
	private Integer page;
	private Integer limit;
}
