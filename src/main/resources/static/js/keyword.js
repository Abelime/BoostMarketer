$('#keyword').keypress(function (event) {
    if (event.which === 13) {
        event.preventDefault();
        insertFetch()
    }
});

$('#keywordRegister').click(function (event) {
    event.preventDefault();
    insertFetch()
});

//키워드 등록
function insertFetch() {
    const keywordName = $('#keyword').val().trim();
    const categoryId = $('#category').val().trim();

    if (keywordName === '') {
        return;
    }

    if (categoryId === '0') {
        alert('카테고리를 선택해주세요.');
        $('#category').focus();
        return;
    }
    FunLoadingBarStart();
    fetch('/keyword', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            keywordName: keywordName,
            categoryId: categoryId
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 응답이 실패했습니다.');
            }
            location.replace('/keyword?pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + categoryId);
        })
        .catch(error => {
            console.error('등록 중 오류가 발생했습니다:', error);
        })
        .finally(() => {
            FunLoadingBarEnd();
        });
}

//키워드 검색
$('.search-keyword').keypress(function (event) {
    if (event.which === 13) {
        event.preventDefault();

        const searchKeyword = $('#search-keyword').val().trim();

        if (searchKeyword === '') {
            return;
        }
        location.replace('/keyword?searchKeyword=' + searchKeyword);
    }
});

//엑셀등록
$("#confirmBtn").click(function () {
    const file = $("#excelFileInput")[0].files[0];
    if (file) {
        $("#uploadModal").css('display', 'none');

        const formData = new FormData();
        formData.append("file", file);

        FunLoadingBarStart();
        fetch('/keyword-excelUpload', {
            method: 'POST',
            body: formData,
            processData: false,
            contentType: false,
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('서버 응답이 실패했습니다.');
                }
                $("#closeBtn").trigger("click");
                location.replace('/keyword?pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + inputCategory);
            })
            .catch(error => {
                console.error('등록 중 오류가 발생했습니다:', error);
            })
            .finally(() => {
                FunLoadingBarEnd();
            });
    } else {
        console.log('파일이 선택되지 않았습니다.');
    }
});

//엑셀 다운
$('#excelDownload').click(function (event) {
    location.href = "/excel"
});

//키워드 삭제
function keywordDelete(keywordIds) {
    Swal.fire({
        title: "삭제하시겠습니까?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "삭제",
        cancelButtonText: '취소',
    }).then((result) => {
        if (result.isConfirmed) {
            fetch('/keyword', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    keywordIds: keywordIds
                })
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('서버 응답이 실패했습니다.');
                    }
                    location.replace('/keyword?pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + inputCategory);
                })
                .catch(error => {
                    console.error('삭제 중 오류가 발생했습니다:', error);
                });
        }
    });
}

//키워드 상위 고정
function keywordFix(keywordId) {

    fetch('/keyword/' + keywordId, {
        method: 'PUT',
    }).then(response => {
        if (!response.ok) {
            throw new Error('서버 응답이 실패했습니다.');
        }
        location.replace('/keyword?pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + inputCategory);
    })
        .catch(error => {
            console.error('등록 중 오류가 발생했습니다:', error);
        });

}

//팝업창
function openPopUp(keywordId) {
    var url = "/keyword/popup/" + keywordId;
    var name = "popup";
    var option = "width = 1100, height = 500, top = 100, left = 200, location = no"
    window.open(url, name, option);
}

window.changePage = function (page) {
    window.location.href = '/keyword?page=' + page + '&pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + inputCategory + '&searchKeyword=' + searchKeyword;
}


document.addEventListener('DOMContentLoaded', function () {
    //체크박스 전체 선택
    const selectAllCheckbox = document.getElementById('selectAll');
    const checkboxes = document.querySelectorAll('.form-check-input[type="checkbox"]');

    selectAllCheckbox.addEventListener('change', function (e) {
        checkboxes.forEach(checkbox => {
            if (checkbox !== selectAllCheckbox) { // Do not change the state of the master checkbox itself
                checkbox.checked = selectAllCheckbox.checked;
            }
        });
    });

    //체크박스 클릭시 button disabled 작동
    const checkboxes2 = $('.form-check-input');

    function updateButtonState() {
        let isAnyChecked = checkboxes2.is(':checked');
        $('#categoryChange, #keywordDelete').prop('disabled', !isAnyChecked);
    }

    // Attach the change event handler to checkboxes
    checkboxes2.on('change', updateButtonState);
    updateButtonState();

    //키워드 카테고리 이동
    $('.category-update').click(function () {
        const category = $(this).data('priority'); // Get the priority from data attribute
        const checkedValues = $('input[name="checkbox"]:checked').map(function () {
            return $(this).val(); // Collect the value of each checked checkbox with name "checkbox"
        }).get(); // Convert the jQuery map to an array
        fetch('keywords', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({category: category, checkboxValues: checkedValues})
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('서버 응답이 실패했습니다.');
                }
                location.replace('/keyword?pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + inputCategory);
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    });

    //키워드 삭제
    $('#keywordDelete').click(function () {
        Swal.fire({
            title: "삭제하시겠습니까?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "삭제",
            cancelButtonText: '취소',
        }).then((result) => {
            if (result.isConfirmed) {
                const checkedValues = $('input[name="checkbox"]:checked').map(function () {
                    return $(this).val(); // Collect the value of each checked checkbox with name "checkbox"
                }).get(); // Convert the jQuery map to an array

                fetch('/keyword', {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({keywordIds: checkedValues})
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('서버 응답이 실패했습니다.');
                        }
                        location.replace('/keyword?pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + inputCategory);
                    })
                    .catch(error => {
                        console.error('Error:', error);
                    });
            }
        });
    });

});

//카테고리 필터
let activeCategory = 0;

function getUrlParameter(name) {
    const url = new URL(window.location.href);
    return url.searchParams.get(name);
}

function setDropdownText(categoryName) {
    $('#dropdownMenuButton').text(categoryName);
}

function updateSelectedCategory() {
    $('.category-filter').removeClass('active');
    $('.category-filter[data-priority="' + activeCategory + '"]').addClass('active');
}

$(document).ready(function () {
    const categoryFromUrl = getUrlParameter('filterCategory');
    if (categoryFromUrl) {
        activeCategory = parseInt(categoryFromUrl);
        const categoryName = $('.category-filter[data-priority="' + categoryFromUrl + '"]').text().trim();
        setDropdownText(categoryName);
    }
    updateSelectedCategory();
});

$('.category-filter').click(function () {
    const filterCategory = $(this).data('priority');
    location.replace('/keyword?pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + inputCategory);
});

// 초기 카테고리 값 저장
const initialCategories = [];
$('.category-input').each(function () {
    initialCategories.push($(this).val().trim());
});

//카테고리 설정
$('#saveChangesBtn').click(function () {
    const categoryName = [];
    const categoryId = [];
    $('.category-input').each(function (index) {
        const newCategoryName = $(this).val().trim();
        const initialCategoryName = initialCategories[index];

        if (newCategoryName !== '' && newCategoryName !== initialCategoryName) {
            categoryId.push(++index);
            categoryName.push(newCategoryName);
        }
    });

    if (categoryName.length > 0) {
        // 서버로 업데이트된 카테고리 전송
        fetch('/keyword/category', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({categoryId: categoryId, categoryName: categoryName})
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('서버 응답이 실패했습니다.');
                }
                location.replace('/keyword?pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + inputCategory);
            })
            .catch(error => {
                console.error('네트워크 오류:', error);
            });
    }
});

$(document).ready(function () {
    var isManaging = false; // 관리 상태를 추적

    $('.keyword-management').click(function () {
        isManaging = !isManaging; // 상태 토글

        if (isManaging) {
            // 키워드 관리 모드 전환
            $(this).html(`
                       <div class="keyword-management" style="display: inline;">
                         <button class="btn px-3" style="background-color: #0080FF; border: none;">
                           <i class="fas fa-stop-circle" style="color: white;"></i>
                           <span style="color: white; font-weight: bold;">관리 종료</span>
                         </button>
                       </div>
                     `); // 아이콘 및 텍스트 변경

            $('.category-change, .keyword-delete').css('display', 'inline'); // 버튼 표시
            $('.form-check-input').css('display', 'inline'); // 버튼 표시
            $('#openModal1, #dropdownMenuButton').prop('disabled', true); // 두 ID 비활성화
        } else {
            // 일반 모드로 전환
            $(this).html(`
                       <div class="me-2 keyword-management" style="display: inline;">
                         <button class="btn text-dark px-3">
                           <i class="fas fa-cog text-success"></i>
                           <span class="fw-bold">키워드 관리</span>
                         </button>
                       </div>
                     `); // 원래 아이콘 및 텍스트

            $('.category-change, .keyword-delete').css('display', 'none'); // 버튼 숨기기
            $('.form-check-input').css('display', 'none'); // 버튼 숨기기
            $('#openModal1, #dropdownMenuButton').prop('disabled', false); // 두 ID 활성화
        }
    });
});

function updateKeyword() {
    FunLoadingBarStart();
    // Fetch API를 사용하여 요청 보내기
    fetch('/keyword-update', {
        method: 'PUT',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 응답이 실패했습니다.');
            }
            location.replace('/keyword?pageSize=' + pageSize + '&filterCategory=' + filterCategory + '&sort=' + sort + '&inputCategory=' + inputCategory);
        })
        .catch(error => {
            console.error('오류가 발생했습니다:', error);
        })
        .finally(() => {
            FunLoadingBarEnd();
        });
}
