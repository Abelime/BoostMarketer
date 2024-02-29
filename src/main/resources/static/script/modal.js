// JavaScript for Keyword Management Modal

// When the user clicks the button, open the modal
function openModal() {
    const modal = document.getElementById("myModal");
    modal.style.display = "flex"; // Change to 'flex' to show the modal
}

// When the user clicks on <span> (x), close the modal
function closeModal() {
    const modal = document.getElementById("myModal");
    modal.style.display = "none"; // Change back to 'none' to hide the modal
    document.getElementById("newKeyword").value = ''; // 입력 필드 초기화
    const keywordsContainer = document.querySelector(".keywords");
    keywordsContainer.innerHTML = ''; // 키워드 컨테이너 초기화
    // 미리 입력된 키워드들을 다시 로드
    loadInitialKeywords();
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    const modal = document.getElementById("myModal");
    if (event.target === modal) {
        closeModal(); // 모달 창 닫기 함수를 호출하여 로직 수행
    }
}

// Add a new keyword chip
function addKeyword() {
    const newKeywordInput = document.getElementById("newKeyword");
    const keywordsContainer = document.querySelector(".keywords");
    const newKeywordValue = newKeywordInput.value.trim();

    // 이미 존재하는 키워드인지 확인
    const existingKeywords = Array.from(keywordsContainer.querySelectorAll(".keyword-chip")).map(chip => chip.textContent.replace(" ×", ""));
    if (newKeywordValue && !existingKeywords.includes(newKeywordValue)) {
        // 키워드가 새로운 경우에만 추가
        const chip = document.createElement("span");
        chip.className = 'keyword-chip';
        chip.textContent = newKeywordValue + " ×";
        chip.onclick = function() { this.remove(); }; // 클릭 시 제거
        keywordsContainer.appendChild(chip);

        // 입력 필드 초기화
        newKeywordInput.value = '';
    } else if (existingKeywords.includes(newKeywordValue)) {
        // 이미 존재하는 키워드일 경우 경고
        alert("동일한 키워드를 추가할 수 없습니다.");
    }
}


// Save keywords
function saveKeywords() {
    const keywordChips = document.querySelectorAll(".keyword-chip");
    const keywords = Array.from(keywordChips).map(chip => chip.textContent.replace(" ×", ""));

    let blogId = document.getElementById('blogId').value;
    let postNo = document.getElementById('postNo').value;

    // 서버에 전송할 데이터를 JSON 형식으로 변환
    const requestData = {
        blogId: blogId,
        postNo: postNo,
        keyWord: keywords // 이전에 'keywords' 대신 'keyWord'로 변경
    };

    // fetch API를 사용하여 서버에 데이터 전송
    fetch('/blog/keyword', { // 여기에 실제 서버의 URL을 입력하세요.
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.text(); // 응답을 JSON 형식으로 파싱
        })
        .then(data => {
            alert(data); // 성공 응답 처리
            location.reload(); // 삭제 후 페이지 새로고침
        })
        .catch(error => {
            console.error('Error:', error); // 오류 처리
        });
}


// Get the button that opens the modal and assign the openModal function
document.querySelector(".keyword-management-button").onclick = openModal;

// Get the <span> element that closes the modal and assign the closeModal function
document.querySelector(".close-button").onclick = closeModal;

// Get the button to add a keyword and assign the addKeyword function
document.querySelector(".add-keyword-button").onclick = addKeyword;

// Get the save button and assign the saveKeywords function
document.querySelector(".save-button").onclick = saveKeywords;

// 엔터 키를 눌렀을 때 addKeyword 함수를 호출합니다.
document.getElementById("newKeyword").addEventListener("keypress", function(event) {
    // event.key는 눌린 키의 값을 나타냅니다. "Enter"는 엔터 키를 의미합니다.
    if (event.key === "Enter") {
        // 폼이 실제로 제출되는 것을 방지합니다.
        event.preventDefault();
        // addKeyword 함수를 호출합니다.
        addKeyword();
    }
});

function loadInitialKeywords() {
    const keywordList = JSON.parse(document.getElementById('keywordList').value);
    const keywordsContainer = document.querySelector(".keywords");
    keywordsContainer.innerHTML = ''; // 기존에 있던 키워드들을 초기화

    keywordList.forEach(keyword => {
        const chip = document.createElement("span");
        chip.className = 'keyword-chip';
        chip.textContent = keyword.keywordName + " ×";
        chip.onclick = function() { this.remove(); }; // 칩 클릭 시 제거
        keywordsContainer.appendChild(chip);
    });

}

document.addEventListener('DOMContentLoaded', function() {
    // 페이지 로딩 시 미리 입력된 키워드들을 로드
    loadInitialKeywords();
});
