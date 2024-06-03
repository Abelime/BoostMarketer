$('#keyword').keypress(function (event) {
  if (event.which === 13) {
    event.preventDefault();

    const keywordName = $('#keyword').val().trim();

    if (keywordName === '') {
      return;
    }
    FunLoadingBarStart();
    window.location.href = '/keyword-analyze?keyword=' + keywordName;
  }
});

//드롭다운
$(document).ready(function () {
  // 화면 로드 시 기본 드롭다운 버튼 텍스트 설정
  $('#dropdownMenuButton').text('일간');

  // 모든 드롭다운 항목 초기화
  $('.dropdown-item').removeClass('active');

  // "일간" 항목을 활성화
  $('.dropdown-item[data-value="일간"]').addClass('active');

  // 드롭다운 항목 클릭 시 버튼 텍스트 변경 및 선택 상태 표시
  $('.dropdown-item').click(function () {
    var selectedText = $(this).data('value'); // 선택한 항목의 텍스트
    $('#dropdownMenuButton').text(selectedText); // 버튼 텍스트 변경

    // 모든 항목의 선택 상태 초기화
    $('.dropdown-item').removeClass('active');

    // 클릭된 항목에 선택 상태 적용
    $(this).addClass('active');
  });
});

//날짜, 달력, 그래프
$(document).ready(function () {
  // 초기값 설정
  setDateRange('일간');

  // 일간, 월간 선택 버튼 클릭 이벤트 핸들러
  $(".dropdown-item").click(function () {
    const mode = $(this).data("value");
    setDateRange(mode);

    let buildParams = () => {
      return {
        'keyword': $("#keyword").val(),
        'dateType_Sel': mode,
        'startDate': $("#startDate").val(),
        'endDate': $("#endDate").val()
      };
    };

    let params = buildParams();
    let queryString = createQueryString(params);

    fetchChartData(queryString)
            .then(dataList => {
              const yConfig = getYAxisConfig(dataList);
              drawChart(ctx, dataList, yConfig);
            });

  });

  // 날짜 범위 설정 함수
  function setDateRange(mode) {
    const today = new Date();
    const startDate = new Date();
    const endDate = new Date();

    if (mode === '월간') {
      startDate.setFullYear(today.getFullYear() - 1, today.getMonth(), 1); // 1년 전 같은 월의 1일
      endDate.setFullYear(today.getFullYear(), today.getMonth(), 0); // 저번 달의 마지막 날
    } else {
      startDate.setMonth(today.getMonth() - 1); //저번달
      endDate.setDate(today.getDate() - 1); // 어제 날짜
    }

    const threeYearsAgo = new Date(today.getFullYear() - 3, 0, 1); // 3년 전 1월 1일 기준

    let initialStartDate = formatDate(startDate, mode);
    let initialEndDate = formatDate(endDate, mode);

    const formattedThreeYearsAgo = formatDate(threeYearsAgo, '일간');

    $('#dateRangeText').text(`${formatMonthRange(initialStartDate, initialEndDate, mode)}`);

    if (mode === '월간') {
      initialStartDate = formatDate(startDate, '');
      initialEndDate = formatDate(endDate, '');
    }

    $('#startDate').val(initialStartDate);
    $('#endDate').val(initialEndDate);

    $("#startDate, #endDate").attr("min", formattedThreeYearsAgo);
    $("#startDate, #endDate").attr("max", initialEndDate);


    // "닫기" 버튼을 클릭하면 초기값으로 복원
    $("#datePickerModal .btn-secondary").click(function () {
      $("#startDate").val(initialStartDate);
      $("#endDate").val(initialEndDate);
    });

    // "적용" 버튼을 클릭하면 초기값을 업데이트
    $("#datePickerModal .btn-primary").click(function () {
      initialStartDate = $("#startDate").val();
      initialEndDate = $("#endDate").val();
      $('#dateRangeText').text(formatMonthRange(initialStartDate, initialEndDate, mode));
      $('#datePickerModal').modal('hide');
      let buildParams = () => {
        return {
          'keyword': $("#keyword").val(),
          'dateType_Sel': mode,
          'startDate': $("#startDate").val(),
          'endDate': $("#endDate").val()
        };
      };

      let params = buildParams();
      let queryString = createQueryString(params);

      fetchChartData(queryString)
              .then(dataList => {
                const yConfig = getYAxisConfig(dataList);
                drawChart(ctx, dataList, yConfig);
              });
    });

    // 시작 날짜 변경 시 마지막 날짜의 최소값을 업데이트
    $("#startDate").on("change", function () {
      var startDate = $(this).val();
      var endDateInput = $("#endDate");

      // 마지막 날짜가 시작 날짜보다 앞지 않도록 제한
      if (endDateInput.val() < startDate) {
        endDateInput.val(startDate);
      }
      endDateInput.attr("min", startDate); // 최소값 업데이트
    });

    // 마지막 날짜 변경 시 시작 날짜의 최대값을 업데이트
    $("#endDate").on("change", function () {
      var endDate = $(this).val();
      var startDateInput = $("#startDate");

      // 시작 날짜가 마지막 날짜보다 뒤지 않도록 제한
      if (startDateInput.val() > endDate) {
        startDateInput.val(endDate);
      }
      startDateInput.attr("max", endDate); // 최대값 업데이트
    });
  }

  function formatDate(date, mode) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    if (mode === '월간') {
      return `${year}-${month}`;
    } else {
      return `${year}-${month}-${day}`;
    }
  }

  function formatMonthRange(startDate, endDate, mode) {
    if (mode === '월간') {
      return `${startDate.split('-')[0]}-${startDate.split('-')[1]} - ${endDate.split('-')[0]}-${endDate.split('-')[1]}`;
    } else {
      return `${startDate} - ${endDate}`;
    }
  }

  let myChart; // 차트 참조 변수

  const ctx = document.getElementById('line-chart').getContext('2d');

  const buildParams = () => {
      return {
          'keyword': $("#keyword").val(),
          'dateType_Sel': '일간',
          'startDate': $("#startDate").val(),
          'endDate': $("#endDate").val()
      };
  };

  const createQueryString = (params) => {
      return Object.keys(params)
          .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
          .join('&');
  };

  const fetchChartData = async (queryString) => {
      try {
          const response = await fetch(`/trendsChart?${queryString}`);
          const dataList = await response.json();
          return dataList;
      } catch (error) {
          console.error('데이터 가져오는 중 오류가 발생했습니다:', error);
          throw error;
      }
  };

  const getYAxisConfig = (dataList) => {
      const minDataValue = Math.min(...dataList.map(data => data['ratio']));
      const maxDataValue = Math.max(...dataList.map(data => data['ratio']));

      const yRange = maxDataValue - minDataValue;
      const yMin = Math.floor(minDataValue / 10) * 10;
      const yMax = Math.ceil(maxDataValue / 10) * 10;
      const stepSize = Math.ceil((yRange + 10) / 7);

      return { yMin, yMax, stepSize };
  };

  const drawChart = (ctx, dataList, yConfig) => {
      if (myChart) { // 기존 차트가 있으면 삭제
          myChart.destroy();
      }

    myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: dataList.map(data => data['period']),
            datasets: [{
                label: $("#keyword").val(),
                data: dataList.map(data => data['ratio']),
                borderColor: 'rgba(199, 135, 235, 1)', // 연보라색 라인
                borderWidth: 3,
                pointRadius: 0,
                pointBackgroundColor: 'rgba(199, 135, 235, 1)',
                spanGaps: true,
            }],
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false,
                },
                tooltip: {
                    intersect: false,
                    mode: 'index',
                    callbacks: {
                        label: (tooltipItem) => `${$("#keyword").val()}: ${Intl.NumberFormat().format(Math.round(tooltipItem.raw))}`, // 정수로 포맷팅
                    },
                },
            },
            scales: {
                x: {
                    grid: {
                        display: false,
                    },
                    ticks: {
                        maxRotation: 0,
                        maxTicksLimit: 7,
                    },
                },
                y: {
                    min: yConfig.yMin,
                    max: yConfig.yMax,
                    ticks: {
                        stepSize: yConfig.stepSize,
                        maxTicksLimit: 7,
                        callback: (value) => new Intl.NumberFormat().format(Math.round(value)), // 정수 및 쉼표 추가
                    },
                    grid: {
                        drawVerticalLine: false,
                        color: 'rgba(0, 0, 0, 0.1)',
                    },
                },
            },
        },
    });

};

  //차트 벡엔드 통신
  const params = buildParams();
  const queryString = createQueryString(params);

  fetchChartData(queryString)
      .then(dataList => {
          const yConfig = getYAxisConfig(dataList);
          drawChart(ctx, dataList, yConfig);
      });

    const fetchRelatedKeywordData = async () => {
      try {
          FunTbodyLoadingBarStart('#tbody-content');
          const param = $("#keyword").val();
          const response = await fetch(`/related-keyword?keyword=`+param);
          const dataList = await response.json();
          return dataList;
      } catch (error) {
          console.error('데이터 가져오는 중 오류가 발생했습니다:', error);
          throw error;
      }
  };

    fetchRelatedKeywordData().then(dataList => {
        populateTable(dataList);
    });

    // 모든 nav-item 요소를 선택
    const smartBlock = $('.smart-block');
    // 첫 번째 nav-item 요소에 active 클래스 추가
    if (smartBlock.length > 0) {
        smartBlock.eq(0).addClass('active');
    }

});

function leftScroll() {
    const container = document.querySelector('.scroll-container');
    container.scrollBy({ left: -200, behavior: 'smooth' });
    updateScrollButtons();
}

function rightScroll() {
    const container = document.querySelector('.scroll-container');
    container.scrollBy({ left: 200, behavior: 'smooth' });
    updateScrollButtons();
}

function updateScrollButtons() {
    const container = document.querySelector('.scroll-container');
    const leftBtn = document.querySelector('.left-btn');
    const rightBtn = document.querySelector('.right-btn');

    if (container != null) {
        setTimeout(() => {
            leftBtn.disabled = container.scrollLeft <= 0;
            rightBtn.disabled = container.scrollWidth <= container.clientWidth + container.scrollLeft;
        }, 250);
    }
}

document.addEventListener('DOMContentLoaded', updateScrollButtons);

function populateTable(dataList) {
    const tbody = document.getElementById('tbody-content');
    tbody.innerHTML = '';  // Clear existing table rows

    dataList.forEach(data => {
        const keywordNameUpper = data.keywordName.replace(/\s+/g, '').toUpperCase();
        const smartBlockListNoSpaces = smartBlockList.map(item => item.replace(/\s+/g, '').toUpperCase());
        const label = smartBlockListNoSpaces.includes(keywordNameUpper)
                      ? '<span class="label-green ms-3">스블</span>'
                      : '<span class="label-orange ms-3">연관</span>';

        const row = `<tr>
            <td class="align-middle text-center d-flex align-items-center">
                ${label}
                <a class="btn btn-link text-dark" href="/keyword-analyze?keyword=${data.keywordName}">
                    <p class="text-xs font-weight-bold mb-0" style="display: inline-block;">${data.keywordName}</p>
                </a>
            </td>
            <td class="align-middle text-center"><p class="text-xs font-weight-bold mb-0">${formatNumber(data.monthSearchPc)}</p></td>
            <td class="align-middle text-center"><p class="text-xs font-weight-bold mb-0">${formatNumber(data.monthSearchMobile)}</p></td>
            <td class="align-middle text-center"><p class="text-xs font-weight-bold mb-0">${formatNumber(data.totalSearch)}</p></td>
            <td class="align-middle text-center"><p class="text-xs font-weight-bold mb-0">${formatNumber(data.monthBlogCnt)}</p></td>
            <td class="align-middle text-center"><p class="text-xs font-weight-bold mb-0">${formatNumber(data.totalBlogCnt)}</p></td>
            <td class="align-middle text-center"><p class="text-xs font-weight-bold mb-0">${formatDecimal(data.blogSaturation)}%</p></td>
        </tr>`;

        tbody.insertAdjacentHTML('beforeend', row);
    });
    FunTbodyLoadingBarEnd();
}

function formatNumber(value) {
    return value.toLocaleString();  // Default locale formatting with commas
}

function formatDecimal(value) {
    return value.toFixed(1);  // Rounds to one decimal place
}


function sortTable(columnIndex) {
    const table = document.querySelector(".keyword-table");
    const tbody = table.querySelector("tbody");
    const rowsArray = Array.from(tbody.rows);
    const headers = table.querySelectorAll("thead tr:nth-child(2) th button");
    const header = headers[columnIndex - 1].parentElement;
    const currentIcon = header.querySelector('.sort-icon');
    let direction = currentIcon.classList.contains('fa-sort-down') ? 'desc' : 'asc';

    rowsArray.sort((a, b) => {
        const aText = a.cells[columnIndex].innerText.replace(/,/g, '').replace('%', '');
        const bText = b.cells[columnIndex].innerText.replace(/,/g, '').replace('%', '');

        const aValue = isNaN(aText) ? aText : Number(aText);
        const bValue = isNaN(bText) ? bText : Number(bText);

        if (direction === 'asc') {
            return aValue > bValue ? 1 : -1;
        } else {
            return aValue < bValue ? 1 : -1;
        }
    });

    tbody.innerHTML = '';

    rowsArray.forEach(row => {
        tbody.appendChild(row);
    });

    headers.forEach(button => {
        const icon = button.querySelector('.sort-icon');
        const text = button.querySelector('span');
        if (icon) {
            icon.classList.remove('fa-sort-down', 'fa-sort-up','text-dark');
            icon.classList.add('fa-sort', 'text-secondary');
        }
        if (text) {
            text.classList.remove('text-dark');
            text.classList.add('text-secondary');
        }
    });

    if (direction === 'asc') {
        currentIcon.classList.remove('fa-sort', 'text-secondary');
        currentIcon.classList.add('fa-sort-down','text-dark');
    } else {
        currentIcon.classList.remove('fa-sort', 'text-secondary');
        currentIcon.classList.add('fa-sort-up','text-dark');
    }

    const currentText = header.querySelector('span');
    if (currentText) {
        currentText.classList.remove('text-secondary');
        currentText.classList.add('text-dark');
    }
}

function getSmartBlockData(index) {
    // Prevent the default action of the event
    if (event) event.preventDefault();

    // Check if the clicked element already has the 'active' class
    if ($('.smart-block').eq(index).hasClass('active')) {
        return; // Do nothing if the link is already active
    }

    $('.smart-block').removeClass('active');
    $('.smart-block').eq(index).addClass('active');

    FunTbodyLoadingBarStart('#content-info-tbody');

    const url = smartBlockHrefList[index];

    // Fetch data from the server
    fetchSmartBlockData(url).then(contentInfoList => {
        contentInfoTable(contentInfoList);
    }).catch(error => {
        console.error('데이터 가져오는 중 오류가 발생했습니다:', error);
    });
}

async function fetchSmartBlockData(url) {
    try {
        const response = await fetch(`/smart-block-contents?link=` + encodeURIComponent(url));
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return await response.json();
    } catch (error) {
        console.error('데이터 가져오는 중 오류가 발생했습니다:', error);
        throw error;
    }
}

function contentInfoTable(contentInfoList) {
    const tbody = document.getElementById('content-info-tbody');
    tbody.innerHTML = '';  // Clear existing table rows

    contentInfoList.forEach(data => {
        let labelHTML = '';

        if (data.type === 'blog' && data.isInfluencer) {
            labelHTML = '<span class="label label-influencer ms-3 me-1">인플루언서</span>';
        } else if (data.type === 'blog' && !data.isInfluencer) {
            labelHTML = '<span class="label label-blog ms-3 me-1">일반 블로그</span>';
        } else if (data.type === 'cafe') {
            labelHTML = '<span class="label label-cafe ms-3 me-1">카페</span>';
        }

        const row = `<tr>
                          <td>
                            <div>
                              ${labelHTML}
                              <span class="text-xs text-secondary font-weight-bolder">${data.author}</span>
                            </div>
                            <div>
                              <a class="btn btn-link text-dark text-xs font-weight-bold mb-0" href="${data.url}" target="_blank">${data.title}</a>
                            </div>
                          </td>
                          <td class="align-middle text-center">
                            <p class="text-dark text-xs font-weight-bold mb-0">${formatNumber(data.textCount)}</p>
                          </td>
                          <td class="align-middle text-center">
                            <p class=" text-dark text-xs font-weight-bold mb-0">${formatNumber(data.imageCount)}</p>
                          </td>
                          <td class="align-middle text-center">
                            <p class=" text-dark text-xs font-weight-bold mb-0">${formatNumber(data.videoCount)}</p>
                          </td>
                          <td class="align-middle text-center">
                            <p class=" text-dark text-xs font-weight-bold mb-0">${formatNumber(data.linkCount)}</p>
                          </td>
                          <td class="align-middle text-center">
                            <p class=" text-dark text-xs font-weight-bold mb-0">${data.date}</p>
                          </td>
                          <td class="align-middle text-center">
                            <p class=" text-dark text-xs font-weight-bold mb-0">${formatNumber(data.visitorCount)}</p>
                          </td>
                          <td class="align-middle text-center">
                            <p class=" text-dark text-xs font-weight-bold mb-0">${formatNumber(data.commentCount)}</p>
                          </td>
                        </tr>`;

        tbody.insertAdjacentHTML('beforeend', row);
        FunTbodyLoadingBarEnd();
    });
}


