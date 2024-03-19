function addKeywordInput() {
    const keywordInputs = document.getElementById('keywordInputs');
    const numInputs = keywordInputs.getElementsByTagName('input').length;

    if (numInputs < 3) {
        const newInputNumber = numInputs + 1;
        const newLabel = document.createElement('label');
        newLabel.setAttribute('for', 'keywordInput' + newInputNumber);
        newLabel.textContent = 'Keyword ' + newInputNumber + ':';

        const newInput = document.createElement('input');
        newInput.setAttribute('type', 'text');
        newInput.setAttribute('id', 'keywordInput' + newInputNumber);
        newInput.setAttribute('name', 'keyWord');
        newInput.style.width = '150px'; // 넓이 설정

        keywordInputs.appendChild(newLabel);
        keywordInputs.appendChild(newInput);
        keywordInputs.appendChild(document.createTextNode(' ')); // 키워드 사이의 공백 추가
    } else {
        alert('최대 3개의 키워드만 추가할 수 있습니다.');
    }
}

function removeKeywordInput() {
    const keywordInputs = document.getElementById('keywordInputs');
    const numInputs = keywordInputs.getElementsByTagName('input').length;

    if (numInputs > 1) { // 최소 두 개의 입력을 유지합니다.
        keywordInputs.removeChild(keywordInputs.lastElementChild); // 입력 제거
        keywordInputs.removeChild(keywordInputs.lastElementChild); // 라벨 제거
        keywordInputs.removeChild(keywordInputs.lastChild); // 공백 제거
    } else {
        alert('최소 하나의 키워드는 있어야 합니다.');
    }
}

function submitForm() {
    const form = document.getElementById('inputForm');
    const formData = new FormData(form);
    const blogUrl = formData.get('blogUrl');

    // 정규식을 사용하여 블로그 ID와 포스트 번호 추출
    const regex = /\/(\w+)\/(\d+)/;
    const matches = blogUrl.match(regex);

    if (matches && matches.length === 3) {
        const blogId = matches[1];
        const postNo = matches[2];

        // FormData에 블로그 ID와 포스트 번호 추가
        formData.append('blogId', blogId);
        formData.append('postNo', postNo);

        fetch('/blog/new', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('서버 오류: ' + response.status);
                }
                alert('저장되었습니다');
                window.location.href="/blog/list"
            })
            .catch(error => {
                console.error('에러 발생:', error);
            });
    } else {
        alert('올바른 URL 형식이 아닙니다.');
    }
}

let chartInstances = []; // 차트 인스턴스를 저장하는 배열
let dates = [];
let keywordList = [];
let ranks = [];
document.addEventListener("DOMContentLoaded", function () {
    // HTML element에서 JSON 문자열을 가져와서 파싱
    keywordList = JSON.parse(document.getElementById('keywordList').value);

    // 특정 키워드 이름 설정
    const keywordName = keywordList[0].keywordName;

    // 모든 keyword의 rankDates에서 date 값을 추출하여 중복 제거된 배열 생성
    dates = [...new Set(keywordList.flatMap(keyword => keyword.rankDates.map(rankDate => rankDate.date)))];


    // 해당 키워드의 rank 값들만 추출
    ranks = keywordList
        .find(keyword => keyword.keywordName === keywordName)?.rankDates.map(rankDate => rankDate.rank) || [];

    // // 초기 차트 데이터
    const chartData = {
        labels: dates,
        datasets: [{
            label: keywordName,
            data: ranks,
            borderColor: 'rgba(255, 99, 132, 1)',
            borderWidth: 2,
            fill: false,
            lineTension: 0.3
        }]
    };

    createChart(chartData);

});

// 키워드 버튼 클릭 시 해당 키워드에 맞는 차트 표시
function showChart(keywordName) {
    var buttons = document.getElementsByClassName("keyword-button");
    for (var i = 0; i < buttons.length; i++) {
        buttons[i].classList.remove("selected"); // 모든 버튼에서 선택된 클래스 제거
    }

    var selectedButton = document.querySelector('button[data-keyword-name="' + keywordName + '"]');
    selectedButton.classList.add("selected"); // 선택된 버튼에 선택된 클래스 추가

    // 현재 스크롤 위치 기억
    var scrollPosition = window.scrollY;

    var ctx = document.getElementById('myChart').getContext('2d');
    var myChart = chartInstances[chartInstances.length - 1]; // 가장 최근에 생성된 차트 인스턴스

    if (myChart) {
        myChart.destroy(); // 가장 최근에 생성된 차트 인스턴스를 파괴
    }

    ranks = keywordList
        .find(keyword => keyword.keywordName === keywordName)?.rankDates.map(rankDate => rankDate.rank) || [];

    let newData = {
        labels: dates,
        datasets: [{
            label: keywordName,
            data: ranks,
            borderColor: 'rgba(255, 99, 132, 1)',
            borderWidth: 2,
            fill: false,
            lineTension: 0.3
        }]
    };

    createChart(newData); // 새로운 차트 생성

    // 스크롤 위치 복원
    window.scrollTo(0, scrollPosition);
}

// 차트 생성 함수
function createChart(data) {
    var maxDataValue = Math.max(...data.datasets[0].data);

    var ctx = document.getElementById('myChart').getContext('2d');
    var myChart = new Chart(ctx, {
        type: 'line',
        data: data,
        options: {
            scales: {
                y: {
                    type: 'linear',
                    reverse: true,
                    min: 0, // 최소값을 0으로 설정
                    max: maxDataValue+1, // 최대값을 30으로 고정
                    position: 'left', // 라벨 위치를 왼쪽으로 설정
                    scaleLabel: {
                        display: true,
                        labelString: '순위'
                    },
                    ticks: {
                        stepSize: 1, // 간격을 1로 설정하여 정수만 표시
                        maxTicksLimit: 2 // 최대 눈금의 수를 2로 제한하여 최대값과 최소값만 보여줌
                    }
                },
                x: {
                    beginAtZero: true,
                    scaleLabel: {
                        display: true,
                        labelString: '날짜'
                    }
                }
            },
            elements: {
                line: {
                    skipNull: true, // 값이 null인 경우 선을 그리지 않음
                    skipZero: true // 값이 0인 경우 선을 그리지 않음
                }
            }
        }
    });

    chartInstances.push(myChart);
}